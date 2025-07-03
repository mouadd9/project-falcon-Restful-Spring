package org.falcon.instanceservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.falcon.instanceservice.enums.InstanceStateEnum;

import java.util.Date;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Instance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String instanceId;
    private Date launchDate;
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStateEnum instanceState;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    // these two references will be used by hibernate to persist changes.
    // typically this happens, let us say user 1 launched a new instance from room 1
    // we create new instance object we retrieve user 1 and room 1 we set them to instance then we persist it after doing this Hibernate will locate the references for room and user in instance and set the foreign keys
}
