package com.falcon.falcon.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flag;
    private String name;
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder.Default
    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
    private Collection<FlagSubmission> flagSubmissions = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
    private Collection<Hint> hints = new ArrayList<>();
}
