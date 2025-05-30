package com.falcon.falcon.entities;
import com.falcon.falcon.enums.Complexity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Added import
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amiId;
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity")
    private Complexity complexity;
    private int totalChallenges;
    private String imageURL;
    private int estimatedTime;
    private LocalDateTime createdAt; // Added field

    // these variables change over time
    private int totalRunningInstances; // whenever a new instance is launched we should go add a number here, when stopped or terminated we reduce a number from here.
    private int totalJoinedUsers; // when a user joins we add a number here when a user leaves a room we reduce a number.
    // the room will be an observer, it will observe the userService , when a user interacts with instances or joins or leave a room we notify the room service so we can change those variables
    
    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Collection<Instance> instances = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Collection<Challenge> challenges = new ArrayList<>();

    // this is useful with cascade.all when a user joins a room, we will retrieve the user and the room then create a membership
    // now after creating the membership how do we persist all this in the database in order for the relationship to be there.
    // now the membership is transient so in order for the entry in the join table to be created we need, to set the room and user in membership and then persist it.
    @Builder.Default   
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY) // room references many memberships but Room doesn't own and manage the relationship
    private Collection<RoomMembership> memberships = new ArrayList<>();
}
