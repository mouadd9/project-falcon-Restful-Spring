package com.falcon.falcon.service.implementations;

import com.falcon.falcon.DTOs.SignUpRequest;
import com.falcon.falcon.DTOs.UserDTO;
import com.falcon.falcon.exceptions.RoleNotFoundException;
import com.falcon.falcon.exceptions.UserAlreadyExistsException;
import com.falcon.falcon.mappers.UserMapper;
import com.falcon.falcon.model.Role;
import com.falcon.falcon.model.User;
import com.falcon.falcon.repository.RoleRepository;
import com.falcon.falcon.repository.UserRepository;
import com.falcon.falcon.service.interfaces.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder bCryptPasswordEncoder;
    private UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    public void validateEmailNotExists(String email) throws UserAlreadyExistsException {
        this.userRepository.findByEmail(email).ifPresent(u -> {
            throw new UserAlreadyExistsException("An account with this email already exists");
        });
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) throws RoleNotFoundException {

        // we create the user i memory
        User newUser = this.userMapper.toEntity(userDTO);
        newUser.setPassword(this.bCryptPasswordEncoder.encode(userDTO.getPassword()));
        // we get the role from the database
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> { throw new RoleNotFoundException("role not found"); });
        // now we will add the role to the collection of roles in the user and viceversa
        newUser.getRoles().add(role);
        role.getUsers().add(newUser);
        User savedUser = this.userRepository.save(newUser);
        roleRepository.save(role);
         // here we save the user.
        return userMapper.toDTO(savedUser);
    }

}
