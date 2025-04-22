package com.falcon.falcon.entity;
import com.falcon.falcon.enums.InstanceStateEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
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
    private InstanceStateEnum instanceState;
    // private String AMIid;

    // Many instances are launched from one Room
    @ManyToOne // this field manages the relationship between room and instance
    @JoinColumn(name = "room_id")
    private Room room; // Each Instance holds a reference to one Room. what we usually do is , we create an instance, then we add it to a list of instance in a room then we save the room.

    // Many instances are launched by one User
    @ManyToOne // this field manages the relationship between user and instance
    @JoinColumn(name = "user_id")
    private User user;


    // these two references will be used by hibernate to persist changes.
    // typically this happens, let us say user 1 launched a new instance from room 1
    // we create new instance object we retrieve user 1 and room 1 we set them to instance then we persist it after doing this Hibernate will locate the references for room and user in instance and set the foreign keys
}
