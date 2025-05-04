package com.falcon.falcon.service.impl;

import com.falcon.falcon.enums.InstanceStateEnum;
import com.falcon.falcon.repository.InstanceRepository;
import com.falcon.falcon.service.InstanceService;
import com.falcon.falcon.statePattern.InstanceState;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InstanceServiceImp implements InstanceService {
    // here we will inject the bean that has state objects
    private InstanceRepository instanceRepository;
    private Map<InstanceStateEnum, InstanceState> stateMap;

    public InstanceServiceImp(Map<InstanceStateEnum, InstanceState> stateMap, InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
        this.stateMap = stateMap;
    }

}
