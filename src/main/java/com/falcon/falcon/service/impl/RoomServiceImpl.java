package com.falcon.falcon.service.impl;

import com.falcon.falcon.dto.RoomDTO;
import com.falcon.falcon.entity.Room;
import com.falcon.falcon.exceptions.roomExceptions.RoomAlreadySavedException;
import com.falcon.falcon.exceptions.roomExceptions.RoomNotFoundException;
import com.falcon.falcon.mapper.ChallengeMapper;
import com.falcon.falcon.mapper.RoomMapper;
import com.falcon.falcon.repository.RoomRepository;
import com.falcon.falcon.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;
    private RoomMapper roomMapper;
    private ChallengeMapper challengeMapper;

    public RoomServiceImpl(RoomRepository roomRepository, RoomMapper roomMapper, ChallengeMapper challengeMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.challengeMapper = challengeMapper;
    }
//     @Transactional(readOnly = true) tells Spring and Hibernate, “This method will only read data from the database, not change it.”
    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> roomMapper.toDTO(room))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(()->new RoomNotFoundException("room not found"));
        RoomDTO roomDTO = roomMapper.toDTO(room);
        roomDTO.setChallenges(room.getChallenges().stream()
                .map(challenge -> challengeMapper.toChallengeDTO(challenge))
                .collect(Collectors.toList()));
        return roomDTO;
    }

    @Override
    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) throws RoomAlreadySavedException {
        // Add debug logging
        System.out.println("Received RoomDTO: " + roomDTO);
        if (roomDTO.getAmiId() == null || roomDTO.getAmiId().trim().isEmpty()) {
            throw new IllegalArgumentException("amiId cannot be null or empty");
        }
        // we check if a room with the same AMI id already exists
        if(roomRepository.existsByAmiId(roomDTO.getAmiId())) {
            throw new RoomAlreadySavedException("Room with the same AMI id or task definition name already exists");
        }

        Room room = roomMapper.toEntity(roomDTO);
        System.out.println("Converted to Room entity: " + room);
        // else we create a new Room object from the DTO passed
        Room savedRoom = roomRepository.save(room);
        System.out.println("Saved Room: " + savedRoom);
        return roomMapper.toDTO(savedRoom);
    }
}

// NOTE 1 :
// a room has only one unique pair of AMIid and taskDefinitionName , these are used to identify the virtual machine or container image in the cloud
// each room has only type of machine, thus only AMI id if using EC2 instances or only one task definition name if using ECS containers
// we need to check if a room with the same AMIid or task definition already exists.