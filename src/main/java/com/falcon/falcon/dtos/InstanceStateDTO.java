package com.falcon.falcon.dtos;

import com.falcon.falcon.entities.Instance;
import com.falcon.falcon.enums.InstanceStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceStateDTO {
    private String instanceId;           // Local database ID
    private String roomId;
    private String userId;
    private InstanceStateEnum lifecycleStatus;
    private String ipAddress;
    private String message;
    private Date createdAt;
    private Date lastStatusUpdateAt;
    
    // Static factory methods for common scenarios
    public static InstanceStateDTO noInstance(String roomId) {
        return InstanceStateDTO.builder()
                .roomId(roomId)
                .lifecycleStatus(InstanceStateEnum.NOT_STARTED)
                .message("No instance found for this room")
                .lastStatusUpdateAt(new Date())
                .build();
    }
    
    public static InstanceStateDTO fromInstance(Instance instance) {
        return InstanceStateDTO.builder()
                .instanceId(instance.getId().toString())
                .roomId(instance.getRoom().getId().toString())
                .userId(instance.getUser().getId().toString())
                .lifecycleStatus(instance.getInstanceState())
                .ipAddress(instance.getIpAddress())
                .createdAt(instance.getLaunchDate())
                .lastStatusUpdateAt(new Date()) // You might want to add this field to Instance entity
                .message(getMessageForState(instance.getInstanceState()))
                .build();
    }
    
    private static String getMessageForState(InstanceStateEnum state) {
        switch (state) {
            case RUNNING:
                return "Instance is running and ready to use";
            case PAUSED:
                return "Instance is stopped but can be restarted";
            case TERMINATED:
                return "Instance has been terminated";
            case NOT_STARTED:
                return "Instance has not been created yet";
            default:
                return "Instance status: " + state.name();
        }
    }
}