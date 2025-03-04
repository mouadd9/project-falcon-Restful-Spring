package com.falcon.falcon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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

}
