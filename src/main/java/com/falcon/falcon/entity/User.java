package com.falcon.falcon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany // owner side is where we configure the relationship.
    @JoinTable( // @JoinTable configures the Join table
            name = "users_roles", // gives a name to the join table
            joinColumns = @JoinColumn(name = "user_id"), // @JoinColumn configures the foreign Keys in the Join Table
            inverseJoinColumns = @JoinColumn(name = "role_id")
    ) // we use @JoinTable/@JoinColumn to manually configure the table and columns names.
    private Collection<Role> roles = new ArrayList<>();

    // cascade.ALL will help us manage RoomMemberships !!
    /*
    The concept is straightforward: we instantiate a Membership entity,
    configure its properties (e.g., user and room),
    add it to the User’s memberships collection, and then persist the User.
    This leverages the cascade = CascadeType.ALL annotation to automatically save the Membership to the database,
    ensuring the relationship is established efficiently.
    */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<RoomMembership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<FlagSubmission> flagSubmissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<Instance> instances = new ArrayList<>();
}
