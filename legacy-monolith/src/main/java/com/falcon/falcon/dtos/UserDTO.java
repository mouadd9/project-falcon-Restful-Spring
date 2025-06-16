package com.falcon.falcon.dtos;

import com.falcon.falcon.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String password;
    private Collection<Role> roles; // Add this field
} // this Object will be returned and used to generate the jwt.
