package org.falcon.instanceservice.service.Imp;


import org.falcon.instanceservice.dto.cloud.CreateInstanceResponse;
import org.falcon.instanceservice.dto.cloud.InstanceActionResponse;
import org.falcon.instanceservice.dto.websocket.InstanceOperationUpdate;
import org.falcon.instanceservice.service.CloudInstanceService;
import org.falcon.instanceservice.service.websocket.InstanceWebSocketService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
// import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
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
public class Ec2Service implements CloudInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(Ec2Service.class);
    private final Ec2AsyncClient ec2AsyncClient;
    private final InstanceWebSocketService webSocketService;

    public Ec2Service(InstanceWebSocketService webSocketService) {
        this.webSocketService = webSocketService;

        NettyNioAsyncHttpClient httpClient = (NettyNioAsyncHttpClient) NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(90)) // This is how long the client will wait when trying to establish a brand new network connection to AWS
                .connectionAcquisitionTimeout(Duration.ofSeconds(180)) // This timeout controls how long you'll wait to "acquire" an available connection from the pool before giving up.
                .maxConcurrency(250) // This sets the maximum number of simultaneous requests the client can handle.
                .readTimeout(Duration.ofSeconds(180)) // Once you've sent your request to AWS and AWS is processing it, this is how long you'll wait for AWS to send back a response.
                .connectionMaxIdleTime(Duration.ofSeconds(300)) // When a connection isn't being used, how long should we keep it open "just in case" before closing it?
                .connectionTimeToLive(Duration.ofMinutes(15)) // Even if a connection is being actively used, this setting forces it to be closed and recreated after 15 minutes. This prevents issues where connections might become stale or where network conditions change over time. 
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

    // Ensures the is properly closed when the Spring application shuts down, releasing resources.
    @PreDestroy // lifecycle hook to clean up resources
    public void cleanup() { 
        // Ensure client is properly closed when application shuts down
        if (ec2AsyncClient != null) {
            logger.info("Closing EC2 Async Client");
            ec2AsyncClient.close();
        }
    }

    // Method to create an EC2 instance : Takes an AMI ID as input.
    public CompletableFuture<CreateInstanceResponse> createInstance(String amiId, String userId, String operationId, String localInstanceId) { // Takes an AMI ID as input.
        logger.info("Creating EC2 instance with AMI ID: {}, User ID: {}, Operation ID: {}, Local Instance ID: {}", amiId, userId, operationId, localInstanceId);

        // Builds a RunInstancesRequest with hardcoded parameters for now
        // Hard-coded parameters for now; amiId is provided as input
        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .instanceType("t2.micro")
                .keyName("juice")
                .networkInterfaces(InstanceNetworkInterfaceSpecification.builder()
                        .associatePublicIpAddress(false) // Public IP association is set to false, this is telling AWS to create this instance in a private subnet without direct internet access. The security group ID specifies the firewall rules that will control what network traffic can reach your instance.
                        .deviceIndex(0)
                        .groups("sg-0d1f4a7d5c3eaa35c")
                        .build())
                .maxCount(1)
                .minCount(1)
                .imageId(amiId)
                .build();

        return ec2AsyncClient.runInstances(runRequest) // this is sending a request to AWS that essentially says "please start building this virtual machine according to these specifications."
        /*
         * The real elegance of this code lies in how it chains these asynchronous operations together using thenCompose. Each operation waits for the previous one to complete before starting the next step. However, importantly, none of these operations block your application's threads.
         */
                .thenCompose(response -> { // AWS immediately responds with a confirmation that includes basic information about the instance, including its assigned instance ID.
                    String cloudEc2InstanceId = response.instances().get(0).instanceId();
                    logger.info("Creating Instance with ID: {}", cloudEc2InstanceId);
                    String privateIpAddress = response.instances().get(0).privateIpAddress();

                    //  3. PROVISIONING (50% progress) - AWS resources allocated
                    webSocketService.sendCustomUpdateToOwner(
                        userId,
                        InstanceOperationUpdate.provisioning(operationId, localInstanceId)
                    );

                    // Continue waiting until instance is running
                    // At this point, AWS has accepted your request and assigned resources, but the actual virtual machine is still being created.
                    logger.info("Waiting for instance to exist: {}", cloudEc2InstanceId);

                    // Note: how do you know when an asynchronous operation has actually completed? The instance creation response tells you the operation has started, but not when it's finished.
                    // The AWS SDK provides elegant "waiter" functionality to solve this problem.
                    // Waiters are specialized tools that repeatedly check the status of a resource until it reaches a desired state.
                    return ec2AsyncClient.waiter() // First waiter
                            .waitUntilInstanceExists(r -> r.instanceIds(cloudEc2InstanceId)) // keeps checking for Instance Existence
                            .thenCompose(waitResponse -> { // if instance exists, we need to wait for it to be in a running state
                                logger.info("Instance exists, waiting for running state: {}", cloudEc2InstanceId);
                                return ec2AsyncClient.waiter() // second waiter
                                        .waitUntilInstanceRunning(r -> r.instanceIds(cloudEc2InstanceId)) // keeps checking for Instance Running
                                        .thenApply(runningResponse -> { // if instance is running, we can return the response
                                            logger.info("Instance is now running: {}", cloudEc2InstanceId);
                                            CreateInstanceResponse resp = CreateInstanceResponse.builder()
                                                .instanceId(cloudEc2InstanceId)
                                                .privateIpAddress(privateIpAddress)
                                                .build();
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
    
    @Override
    public CompletableFuture<InstanceActionResponse> stopInstance(String instanceId, String userId, String operationId, String localInstanceId) {
        logger.info("Stopping EC2 instance: {}", instanceId);
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.stopInstances(request)
                .thenCompose(response -> {
                    logger.info("Stop request accepted for instance: {}, waiting for instance to actually stop...", instanceId);
                    
                    // Send intermediate update - stopping in progress
                    webSocketService.sendCustomUpdateToOwner(
                        userId,
                        InstanceOperationUpdate.stopping(operationId, localInstanceId)
                    );
                    
                    // ✅ WAIT for the instance to actually stop before returning success
                    return ec2AsyncClient.waiter()
                            .waitUntilInstanceStopped(r -> r.instanceIds(instanceId))
                            .thenApply(waitResponse -> {
                                logger.info("Instance {} has actually stopped", instanceId);
                                
                                InstanceActionResponse actionResponse = new InstanceActionResponse();
                                actionResponse.setInstanceId(instanceId);
                                return actionResponse;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to stop instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new CompletionException(cause);
                    }
                });
    }

    @Override
    public CompletableFuture<InstanceActionResponse> startInstance(String instanceId, String userId, String operationId, String localInstanceId) {
        logger.info("Starting EC2 instance: {}", instanceId);
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.startInstances(request)
                .thenCompose(response -> {
                    logger.info("Successfully started instance: {}", instanceId);
                    //  3. STARTING (50% progress) - AWS resources allocated
                    webSocketService.sendCustomUpdateToOwner(
                        userId,
                        InstanceOperationUpdate.starting(operationId, localInstanceId)
                    );
                    // Create a request to get the instance details including IP address
                    // the IP address changes !!!!!
                    DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
                            .instanceIds(instanceId)
                            .build();

                    // Return a new future that will complete when we have the IP address
                    return ec2AsyncClient.describeInstances(describeRequest)
                            .thenApply(describeResponse -> {
                                String privateIp = null;

                                // Extract private IP address if available
                                if (!describeResponse.reservations().isEmpty() && !describeResponse.reservations().get(0).instances().isEmpty()) {
                                    privateIp = describeResponse.reservations().get(0).instances().get(0).privateIpAddress();
                                    logger.info("Retrieved private IP address {} for instance {}", privateIp, instanceId);
                                } else {
                                   throw new RuntimeException("No IP address found for instance " + instanceId);
                                }

                                InstanceActionResponse actionResponse = InstanceActionResponse.builder()
                                        .instanceId(instanceId)
                                        .privateIpAddress(privateIp)
                                        .build();

                                return actionResponse;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to start instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new CompletionException(cause);
                    }
                });
    }

    @Override
    public CompletableFuture<InstanceActionResponse> terminateInstance(String instanceId, String userId, String operationId, String localInstanceId) {
        logger.info("Terminating EC2 instance: {}", instanceId);
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.terminateInstances(request)
                .thenCompose(response -> {
                    logger.info("Terminate request accepted for instance: {}, waiting for instance to actually terminate...", instanceId);
                    
                    // Send intermediate update
                    webSocketService.sendCustomUpdateToOwner(
                        userId,
                        InstanceOperationUpdate.terminating(operationId, localInstanceId)
                    );
                    
                    // ✅ WAIT for the instance to actually terminate
                    return ec2AsyncClient.waiter()
                            .waitUntilInstanceTerminated(r -> r.instanceIds(instanceId))
                            .thenApply(waitResponse -> {
                                logger.info("Instance {} has actually terminated", instanceId);
                                
                                InstanceActionResponse actionResponse = InstanceActionResponse.builder()
                                        .instanceId(instanceId)
                                        .build();
                                return actionResponse;
                            });
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to terminate instance {}: {}", instanceId, throwable.getMessage(), throwable);
                    Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    } else {
                        throw new CompletionException(cause);
                    }
                });
    }
}


