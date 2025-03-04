package com.falcon.falcon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // here we only reference where we configured the join table
    // so that JPA knows what join table to use to get users associated to a role.
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new ArrayList<>();
}
