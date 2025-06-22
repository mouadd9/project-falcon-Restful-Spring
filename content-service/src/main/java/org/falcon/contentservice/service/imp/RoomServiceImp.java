package org.falcon.contentservice.service.imp;

import org.falcon.contentservice.dto.RoomDTO;
import org.falcon.contentservice.entity.Room;
import org.falcon.contentservice.exception.RoomAlreadySavedException;
import org.falcon.contentservice.exception.RoomNotFoundException;
import org.falcon.contentservice.mapper.ChallengeMapper;
import org.falcon.contentservice.mapper.RoomMapper;
import org.falcon.contentservice.repository.RoomRepository;
import org.falcon.contentservice.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImp implements RoomService {
    private RoomRepository roomRepository;
    private RoomMapper roomMapper;
    private ChallengeMapper challengeMapper;

    public RoomServiceImp(RoomRepository roomRepository, RoomMapper roomMapper, ChallengeMapper challengeMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.challengeMapper = challengeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> roomMapper.toDTO(room))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getRoomsByIds(List<Long> roomIds) {
        List<Room> rooms = roomRepository.findAllById(roomIds);
        return rooms.stream()
                .map(room -> roomMapper.toDTO(room))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findRoomWithChallengesById(id)
                .orElseThrow(()->new RoomNotFoundException("Room with ID " + id + " not found."));
        RoomDTO roomDTO = roomMapper.toDTO(room);
        roomDTO.setChallenges(
                room.getChallenges().stream()
                        .map(challenge -> challengeMapper.toChallengeDTO(challenge))
                        .collect(Collectors.toList())
        );
        return roomDTO;
    }

    @Override
    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) {
        if (roomDTO.getAmiId() == null || roomDTO.getAmiId().trim().isEmpty()) {
            throw new IllegalArgumentException("amiId cannot be null or empty");
        }
        if (roomRepository.existsByAmiId(roomDTO.getAmiId())) {
            throw new RoomAlreadySavedException("Room with the same AMI id or task definition name already exists");
        }
        Room room = roomMapper.toEntity(roomDTO);
        room.setCreatedAt(LocalDateTime.now());
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDTO(savedRoom);
    }

    @Override
    public void incrementJoinedUsers(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new RoomNotFoundException("room not found"));
        room.setTotalJoinedUsers(room.getTotalJoinedUsers() + 1);
        roomRepository.save(room);
    }

    @Override
    public void decrementJoinedUsers(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new RoomNotFoundException("room not found"));
        room.setTotalJoinedUsers(room.getTotalJoinedUsers() - 1);
        roomRepository.save(room);
    }
}
