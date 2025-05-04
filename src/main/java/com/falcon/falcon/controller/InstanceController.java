package com.falcon.falcon.controller;

import com.falcon.falcon.service.InstanceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instances")
public class InstanceController {
    private final InstanceService instanceService;
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }
}
