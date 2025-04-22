package com.falcon.falcon.dto;

import com.falcon.falcon.enums.InstanceStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceDTO {
    private Long id;

    private String ipAddress;
    private String instanceId;
    private Date launchDate;
    private Date expirationDate;
    private InstanceStateEnum instanceState;
}
