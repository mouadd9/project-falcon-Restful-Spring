package com.falcon.falcon.dtos.cloudDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceActionResponse {
    private String instanceId;
    private String privateIpAddress;
}