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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
    private Collection<FlagSubmission> flagSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY)
    private Collection<Hint> hints = new ArrayList<>();
}
