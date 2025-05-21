package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.cloudDto.CreateInstanceResponse;
import com.falcon.falcon.dtos.cloudDto.InstanceActionRequest;
import com.falcon.falcon.dtos.cloudDto.InstanceActionResponse;
import com.falcon.falcon.services.impl.Ec2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/ec2")
public class EC2Controller {

    private static final Logger logger = LoggerFactory.getLogger(EC2Controller.class);

    @Autowired
    private Ec2Service ec2Service;

    @PostMapping("/create")
    public CompletableFuture<CreateInstanceResponse> createInstance(@RequestParam String amiId) {
        logger.info("Received request to create instance with AMI ID: {}", amiId);
        return ec2Service.createInstance(amiId)
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error in create instance response: {}", throwable.getMessage());
                    } else {
                        logger.info("Successfully created instance: {}", response.getInstanceId());
                    }
                });
    }

    @PostMapping("/stop")
    public CompletableFuture<InstanceActionResponse> stopInstance(@RequestBody InstanceActionRequest request) {
        logger.info("Received request to stop instance: {}", request.getInstanceId());
        return ec2Service.stopInstance(request.getInstanceId())
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error stopping instance: {}", throwable.getMessage());
                    } else {
                        logger.info("Stop instance response: {}", response.getMessage());
                    }
                });
    }

    @PostMapping("/start")
    public CompletableFuture<InstanceActionResponse> startInstance(@RequestBody InstanceActionRequest request) {
        logger.info("Received request to start instance: {}", request.getInstanceId());
        return ec2Service.startInstance(request.getInstanceId())
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error starting instance: {}", throwable.getMessage());
                    } else {
                        logger.info("Start instance response: {}", response.getMessage());
                    }
                });
    }

    @PostMapping("/terminate")
    public CompletableFuture<InstanceActionResponse> terminateInstance(@RequestBody InstanceActionRequest request) {
        logger.info("Received request to terminate instance: {}", request.getInstanceId());
        return ec2Service.terminateInstance(request.getInstanceId())
                .whenComplete((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error terminating instance: {}", throwable.getMessage());
                    } else {
                        logger.info("Terminate instance response: {}", response.getMessage());
                    }
                });
    }
}
