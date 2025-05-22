package com.falcon.falcon.services.impl;

import com.falcon.falcon.dtos.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.services.CloudInstanceService;
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

    /* 
     * This client allows you to perform EC2 operations (like managing instances) without blocking your application's execution.
     * All operations return CompletableFuture objects, making it perfect for high-throughput applications.
    */
    private final Ec2AsyncClient ec2AsyncClient;

    public Ec2Service() {

        /*
        ## Explanation of the HTTP Client and Async Client
         * An HTTP client takes your request to communicate with a web service (like AWS)
         * and handles all the technical details of sending it over the internet and bringing back the response. 
         * 
         * When your Java application needs to talk to AWS EC2 services, it can't just magically communicate.
         * It needs to package your request into the proper HTTP format,
         * establish a network connection to AWS servers, send the data, wait for a response,
         * and then decode that response back into something your Java code can understand.
         * 
         * The NettyNioAsyncHttpClient is a specific type of HTTP client built on top of Netty,
         * which is a high-performance networking framework.
         * 
         * The "Async" part is crucial here.
         * Instead of your thread sitting idle waiting for AWS to respond (which could take seconds),
         * an async client frees up your thread to do other work. When AWS eventually responds,
         * the client notifies your code through callbacks or CompletableFutures.
         * 
        ## Connection Pooling: The Foundation
         * Before diving into the specific settings, you need to understand connection pooling.
         * Establishing a new network connection to AWS for every single request would be incredibly slow,
         * like having to introduce yourself to someone every time you want to say something to them.
         * 
         * Instead, HTTP clients maintain a "pool" of established connections that can be reused.
         * Think of it as having several phone lines open to AWS that multiple conversations can use.
         * The client manages these connections, deciding when to create new ones, when to reuse existing ones, and when to close old ones.
        */

        /*
         * 
        ## The Thread Management Magic
         *  Here's what makes this particularly elegant: 
         *    while your main application thread continues working on other tasks,
         *    the HTTP client uses a separate pool of threads to handle the network communication.
         *    When AWS responds, one of these background threads executes your callback.
         *    This means your application can handle hundreds or thousands of concurrent AWS requests without needing hundreds or thousands of threads.
         */

        // Enhanced connection settings to make the communication with AWS more robust and prevent premature timeouts
        // This configuration prioritizes reliability over speed
        NettyNioAsyncHttpClient httpClient = (NettyNioAsyncHttpClient) NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(90)) // This is how long the client will wait when trying to establish a brand new network connection to AWS
                .connectionAcquisitionTimeout(Duration.ofSeconds(180)) // This timeout controls how long you'll wait to "acquire" an available connection from the pool before giving up.
                .maxConcurrency(250) // This sets the maximum number of simultaneous requests the client can handle.
                .readTimeout(Duration.ofSeconds(180)) // Once you've sent your request to AWS and AWS is processing it, this is how long you'll wait for AWS to send back a response.
                .connectionMaxIdleTime(Duration.ofSeconds(300)) // When a connection isn't being used, how long should we keep it open "just in case" before closing it?
                .connectionTimeToLive(Duration.ofMinutes(15)) // Even if a connection is being actively used, this setting forces it to be closed and recreated after 15 minutes. This prevents issues where connections might become stale or where network conditions change over time. 
                .build();


        /*
         * 
         *   if an API call to AWS fails (e.g., due to transient network issues or throttling),
         *   the SDK will automatically retry the call multiple times (up to 8 retries)
         *   with increasing delays between attempts (exponential backoff with jitter to prevent thundering herd problems).
         *   This significantly improves the resilience of EC2 operations.
         */

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
    /*
     * The method signature tells us immediately that this is an asynchronous operation.
     * It returns CompletableFuture<CreateInstanceResponse> rather than just CreateInstanceResponse.
     * This is crucial because creating an EC2 instance isn't instantaneous.
     * It's a complex process that can take anywhere from 30 seconds to several minutes,
     * depending on the instance type, AMI size, and current AWS load.
     */

    /*
     * By returning a CompletableFuture, this method follows the same non-blocking pattern we discussed earlier.
     * The calling code can initiate the instance creation and then continue with other work while AWS handles
     * the heavy lifting of actually provisioning the virtual machine.
     */
    public CompletableFuture<CreateInstanceResponse> createInstance(String amiId) { // Takes an AMI ID as input.
        logger.info("Creating EC2 instance with AMI ID: {}", amiId);

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

        // Uses the ec2AsyncClient to asynchronously launch the instance.   
          // The runInstances method is non-blocking and returns a CompletableFuture
          // This means the method will return immediately, and the actual instance creation will happen in the background.
          // The CompletableFuture will be completed when the instance is created or if an error occurs.
        return ec2AsyncClient.runInstances(runRequest) // this is sending a request to AWS that essentially says "please start building this virtual machine according to these specifications."
        /*
         * The real elegance of this code lies in how it chains these asynchronous operations together using thenCompose. Each operation waits for the previous one to complete before starting the next step. However, importantly, none of these operations block your application's threads.
         */
                .thenCompose(response -> { // AWS immediately responds with a confirmation that includes basic information about the instance, including its assigned instance ID.
                    String instanceIdVal = response.instances().get(0).instanceId();
                    logger.info("Creating Instance with ID: {}", instanceIdVal);
                    String privateIpAddress = response.instances().get(0).privateIpAddress();

                    // Continue waiting until instance is running
                    // At this point, AWS has accepted your request and assigned resources, but the actual virtual machine is still being created.
                    logger.info("Waiting for instance to exist: {}", instanceIdVal);

                    // Note: how do you know when an asynchronous operation has actually completed? The instance creation response tells you the operation has started, but not when it's finished.
                    // The AWS SDK provides elegant "waiter" functionality to solve this problem.
                    // Waiters are specialized tools that repeatedly check the status of a resource until it reaches a desired state.
                    return ec2AsyncClient.waiter() // First waiter
                            .waitUntilInstanceExists(r -> r.instanceIds(instanceIdVal)) // keeps checking for Instance Existence
                            .thenCompose(waitResponse -> { // if instance exists, we need to wait for it to be in a running state
                                logger.info("Instance exists, waiting for running state: {}", instanceIdVal);
                                return ec2AsyncClient.waiter() // second waiter
                                        .waitUntilInstanceRunning(r -> r.instanceIds(instanceIdVal)) // keeps checking for Instance Running
                                        .thenApply(runningResponse -> { // if instance is running, we can return the response
                                            logger.info("Instance is now running: {}", instanceIdVal);
                                            CreateInstanceResponse resp = CreateInstanceResponse.builder()
                                                .instanceId(instanceIdVal)
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
    public CompletableFuture<InstanceActionResponse> stopInstance(String instanceId) {
        logger.info("Stopping EC2 instance: {}", instanceId);
        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.stopInstances(request)
                .thenApply(response -> {
                    logger.info("Successfully stopped instance: {}", instanceId);

                    InstanceActionResponse actionResponse = new InstanceActionResponse();
                    actionResponse.setInstanceId(instanceId);
                    return actionResponse;
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
    public CompletableFuture<InstanceActionResponse> startInstance(String instanceId) {
        logger.info("Starting EC2 instance: {}", instanceId);
        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.startInstances(request)
                .thenCompose(response -> {
                    logger.info("Successfully started instance: {}", instanceId);

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
    public CompletableFuture<InstanceActionResponse> terminateInstance(String instanceId) {
        logger.info("Terminating EC2 instance: {}", instanceId);
        TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        return ec2AsyncClient.terminateInstances(request)
                .thenApply(response -> {
                    logger.info("Successfully terminated instance: {}", instanceId);

                    InstanceActionResponse actionResponse = InstanceActionResponse.builder()
                            .instanceId(instanceId)
                            .build();
                    return actionResponse;
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


