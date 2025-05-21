package com.falcon.falcon.service.impl;

import com.falcon.falcon.dto.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dto.cloudDto.InstanceActionResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.InstanceNetworkInterfaceSpecification;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.FullJitterBackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class Ec2Service {

    private static final Logger logger = LoggerFactory.getLogger(Ec2Service.class);
    private final Ec2AsyncClient ec2AsyncClient;

    public Ec2Service() {
        // Enhanced connection settings to address timeout issues
        NettyNioAsyncHttpClient httpClient = (NettyNioAsyncHttpClient) NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(90))       // Changed from connectTimeout
                .connectionAcquisitionTimeout(Duration.ofSeconds(180))
                .maxConcurrency(250)
                .readTimeout(Duration.ofSeconds(180))            // Changed from socketTimeout
                .connectionMaxIdleTime(Duration.ofSeconds(300))
                .connectionTimeToLive(Duration.ofMinutes(15))    // Corrected method name
                .build();

        // Create a custom retry policy with exponential backoff
        BackoffStrategy backoffStrategy = FullJitterBackoffStrategy.builder()
                .baseDelay(Duration.ofMillis(500))              // Increased from 100
                .maxBackoffTime(Duration.ofSeconds(30))         // Increased from 20
                .build();

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .numRetries(8)                                 // Increased from 5
                .backoffStrategy(backoffStrategy)
                .throttlingBackoffStrategy(backoffStrategy)
                .retryCondition(RetryCondition.defaultRetryCondition())
                .build();

        // Initialize client with improved configuration
        this.ec2AsyncClient = Ec2AsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(b -> b
                        .retryPolicy(retryPolicy)
                        .apiCallTimeout(Duration.ofMinutes(10))     // Increased from 5
                        .apiCallAttemptTimeout(Duration.ofMinutes(3))) // Increased from 2
                .build();

        logger.info("EC2 Async Client initialized with enhanced timeout settings");
    }

    @PreDestroy
    public void cleanup() {
        // Ensure client is properly closed when application shuts down
        if (ec2AsyncClient != null) {
            logger.info("Closing EC2 Async Client");
            ec2AsyncClient.close();
        }
    }

    public CompletableFuture<CreateInstanceResponse> createInstance(String amiId) {
        logger.info("Creating EC2 instance with AMI ID: {}", amiId);

        // Hard-coded parameters for now; amiId is provided as input
        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .instanceType("t2.micro")
                .keyName("juice")
                .networkInterfaces(InstanceNetworkInterfaceSpecification.builder()
                        .associatePublicIpAddress(false)
                        .deviceIndex(0)
                        .groups("sg-0d1f4a7d5c3eaa35c")
                        .build())
                .maxCount(1)
                .minCount(1)
                .imageId(amiId)
                .build();

        return ec2AsyncClient.runInstances(runRequest)
                .thenCompose(response -> {
                    String instanceIdVal = response.instances().get(0).instanceId();
                    logger.info("Instance created with ID: {}", instanceIdVal);
                    String privateIpAddress = response.instances().get(0).privateIpAddress();

                    // Continue waiting until instance is running
                    logger.info("Waiting for instance to exist: {}", instanceIdVal);
                    return ec2AsyncClient.waiter()
                            .waitUntilInstanceExists(r -> r.instanceIds(instanceIdVal))
                            .thenCompose(waitResponse -> {
                                logger.info("Instance exists, waiting for running state: {}", instanceIdVal);
                                return ec2AsyncClient.waiter()
                                        .waitUntilInstanceRunning(r -> r.instanceIds(instanceIdVal))
                                        .thenApply(runningResponse -> {
                                            logger.info("Instance is now running: {}", instanceIdVal);
                                            CreateInstanceResponse resp = new CreateInstanceResponse();
                                            resp.setInstanceId(instanceIdVal);
                                            resp.setPrivateIpAddress(privateIpAddress);
                                            return resp;
                                        });
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to create EC2 instance: {}", throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;
                    throw new RuntimeException("Failed to create EC2 instance: " + cause.getMessage(), cause);
                });
    }
    public CompletableFuture<InstanceActionResponse> stopInstance(String instanceId) {
        logger.info("Stopping EC2 instance: {}", instanceId);
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.stopInstances(request)
                .thenApply(response -> {
                    logger.info("Successfully stopped instance: {}", instanceId);
                    String currentState = !response.stoppingInstances().isEmpty() ?
                            String.valueOf(response.stoppingInstances().get(0).currentState().name()) :
                            "unknown";

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    actionResponse.setSuccess(true);
                    actionResponse.setMessage("Instance stopped successfully");
                    actionResponse.setStatus(currentState);
                    return actionResponse;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to stop instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    actionResponse.setSuccess(false);
                    actionResponse.setMessage("Failed to stop instance: " + cause.getMessage());
                    return actionResponse;
                });
    }

    public CompletableFuture<InstanceActionResponse> startInstance(String instanceId) {
        logger.info("Starting EC2 instance: {}", instanceId);
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.startInstances(request)
                .thenCompose(response -> {
                    logger.info("Successfully started instance: {}", instanceId);
                    String currentState = !response.startingInstances().isEmpty() ?
                            String.valueOf(response.startingInstances().get(0).currentState().name()) :
                            "unknown";

                    // Create a request to get the instance details including IP address
                    DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
                            .instanceIds(instanceId)
                            .build();

                    // Return a new future that will complete when we have the IP address
                    return ec2AsyncClient.describeInstances(describeRequest)
                            .thenApply(describeResponse -> {
                                String privateIp = null;

                                // Extract private IP address if available
                                if (!describeResponse.reservations().isEmpty() &&
                                        !describeResponse.reservations().get(0).instances().isEmpty()) {
                                    privateIp = describeResponse.reservations().get(0).instances().get(0).privateIpAddress();
                                    logger.info("Retrieved private IP address {} for instance {}", privateIp, instanceId);
                                }

                                InstanceActionResponse actionResponse = new InstanceActionResponse();
                                actionResponse.setInstanceId(instanceId);
                                actionResponse.setSuccess(true);
                                actionResponse.setMessage("Instance started successfully");
                                actionResponse.setStatus(currentState);
                                actionResponse.setPrivateIpAddress(privateIp);
                                return actionResponse;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to start instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    actionResponse.setSuccess(false);
                    actionResponse.setMessage("Failed to start instance: " + cause.getMessage());
                    return actionResponse;
                });
    }

    public CompletableFuture<InstanceActionResponse> terminateInstance(String instanceId) {
        logger.info("Terminating EC2 instance: {}", instanceId);
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.terminateInstances(request)
                .thenApply(response -> {
                    logger.info("Successfully terminated instance: {}", instanceId);
                    String currentState = !response.terminatingInstances().isEmpty() ?
                            String.valueOf(response.terminatingInstances().get(0).currentState().name()) :
                            "unknown";

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    actionResponse.setSuccess(true);
                    actionResponse.setMessage("Instance terminated successfully");
                    actionResponse.setStatus(currentState);
                    return actionResponse;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to terminate instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    actionResponse.setSuccess(false);
                    actionResponse.setMessage("Failed to terminate instance: " + cause.getMessage());
                    return actionResponse;
                });
    }
}


