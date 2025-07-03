
package org.falcon.instanceservice.dto.cloud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.falcon.instanceservice.enums.InstanceStateEnum;

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