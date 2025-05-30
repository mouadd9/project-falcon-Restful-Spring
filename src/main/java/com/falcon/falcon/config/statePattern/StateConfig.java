package com.falcon.falcon.config.statePattern;

import com.falcon.falcon.enums.InstanceStateEnum;
import com.falcon.falcon.statePattern.InstanceState;
import com.falcon.falcon.statePattern.impl.NotStartedState;
import com.falcon.falcon.statePattern.impl.PausedState;
import com.falcon.falcon.statePattern.impl.RunningState;
import com.falcon.falcon.statePattern.impl.TerminatedState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

// this class will provide a bean, this bean is a map that maps InstanceStateEnum values to Concrete Implementations of the InstanceState Interface
// each State Object "Concrete impl" describes the behavior of an instance in a particular state.
@Configuration
public class StateConfig {

    private final NotStartedState notStartedState;
    private final PausedState pausedState;
    private final RunningState runningState;
    private final TerminatedState terminatedState;

    public StateConfig(NotStartedState notStartedState, PausedState pausedState, RunningState runningState, TerminatedState terminatedState) {
        this.notStartedState = notStartedState;
        this.pausedState = pausedState;
        this.runningState = runningState;
        this.terminatedState = terminatedState;
    }

    @Bean
    public Map<InstanceStateEnum, InstanceState> stateMap() {
        Map<InstanceStateEnum, InstanceState> map = new HashMap<>();
        map.put(InstanceStateEnum.NOT_STARTED, notStartedState);
        map.put(InstanceStateEnum.RUNNING, runningState);
        map.put(InstanceStateEnum.PAUSED, pausedState);
        map.put(InstanceStateEnum.TERMINATED, terminatedState);
        return map;
    }
}
