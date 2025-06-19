package org.falcon.contentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.falcon.contentservice.enums.Complexity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity @Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String amiId;
    private String title;
    @Lob @Column(columnDefinition = "LONGTEXT")
    private String description;
    @Enumerated(EnumType.STRING) @Column(name = "complexity")
    private Complexity complexity;
    private int totalChallenges;
    private String imageURL;
    private int estimatedTime;
    private LocalDateTime createdAt;
    private int totalJoinedUsers;

    @Builder.Default @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Collection<Challenge> challenges = new ArrayList<>();
}