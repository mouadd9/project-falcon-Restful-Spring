package com.falcon.falcon.repository;

import com.falcon.falcon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // we will use derived query method provided by Spring Data JPA
    // in this example we want to check if a user with a given email exists
    // the naming is not arbitrary, it must follow certain rules
    boolean existsByEmail(String email); // returns true if there is at least one entity in the database with the given email, and false otherwise
    Optional<User> findByEmail(String email); // this returns an Optional that may or may not have a user
}
