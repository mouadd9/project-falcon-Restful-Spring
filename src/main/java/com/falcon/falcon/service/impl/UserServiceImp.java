package com.falcon.falcon.service.impl;

import com.falcon.falcon.dto.UserDTO;
import com.falcon.falcon.exceptions.userExceptions.RoleNotFoundException;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;
import com.falcon.falcon.mapper.ChallengeMapper;
import com.falcon.falcon.mapper.RoomMapper;
import com.falcon.falcon.mapper.UserMapper;
import com.falcon.falcon.entity.Role;
import com.falcon.falcon.entity.User;
import com.falcon.falcon.repository.RoleRepository;
import com.falcon.falcon.repository.RoomMembershipRepository;
import com.falcon.falcon.repository.RoomRepository;
import com.falcon.falcon.repository.UserRepository;
import com.falcon.falcon.service.UserService;
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

    // set for review !!
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


}
