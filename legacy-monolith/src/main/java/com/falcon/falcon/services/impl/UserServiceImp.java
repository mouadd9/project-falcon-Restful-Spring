package com.falcon.falcon.services.impl;

import com.falcon.falcon.dtos.UserDTO;
import com.falcon.falcon.exceptions.userExceptions.RoleNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;
import com.falcon.falcon.mappers.ChallengeMapper;
import com.falcon.falcon.mappers.RoomMapper;
import com.falcon.falcon.mappers.UserMapper;
import com.falcon.falcon.entities.Role;
import com.falcon.falcon.entities.User;
import com.falcon.falcon.repositories.RoleRepository;
import com.falcon.falcon.repositories.RoomMembershipRepository;
import com.falcon.falcon.repositories.RoomRepository;
import com.falcon.falcon.repositories.UserRepository;
import com.falcon.falcon.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder bCryptPasswordEncoder;
    private UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository, RoomRepository roomRepository, RoomMembershipRepository roomMembershipRepository, RoleRepository roleRepository, PasswordEncoder bCryptPasswordEncoder, UserMapper userMapper, RoomMapper roomMapper, ChallengeMapper challengeMapper) {
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
        User newUser = this.userMapper.toEntity(userDTO);
        newUser.setPassword(this.bCryptPasswordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> { throw new RoleNotFoundException("role not found"); });
        // now we will add the role to the collection of roles in the user and viceversa
        newUser.getRoles().add(role);
        role.getUsers().add(newUser);
        User savedUser = this.userRepository.save(newUser);
        roleRepository.save(role);
         // here we save the user.
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO getUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return userMapper.toDTO(user);
    }
}
