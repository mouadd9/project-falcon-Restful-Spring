
package com.falcon.falcon.dtos.cloud;

import com.falcon.falcon.enums.InstanceStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstanceResponse {
    private Long internalInstanceId;
    private InstanceStateEnum instanceState;
    private String instanceId;
    private String privateIpAddress;
}