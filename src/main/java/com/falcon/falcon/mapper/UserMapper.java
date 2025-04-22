package com.falcon.falcon.mapper;

import com.falcon.falcon.dto.UserDTO;
import com.falcon.falcon.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data @AllArgsConstructor
public class UserMapper {

    // after retrieving entities we map them to DTOs
    public UserDTO toDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }

    // we map data access objects coming from the presentation layer to persisting Entities
    public User toEntity(UserDTO userDTO){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
