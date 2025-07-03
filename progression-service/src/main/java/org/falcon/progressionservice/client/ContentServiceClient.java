package org.falcon.progressionservice.client;

import org.falcon.progressionservice.client.dto.ChallengeWithSolutionDTO;
import org.falcon.progressionservice.client.dto.RoomDTO;
import org.falcon.progressionservice.exception.CustomContentServiceErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "content-service", configuration = CustomContentServiceErrorDecoder.class)
public interface ContentServiceClient {
    @GetMapping("/api/content/rooms")
    List<RoomDTO> getAllRooms();

    @GetMapping("/api/content/rooms")
    List<RoomDTO> getRoomsByIds(@RequestParam("ids") List<Long> roomIds);

    @GetMapping("/api/content/rooms/{roomId}")
    RoomDTO getRoomById(@PathVariable("roomId") Long roomId);

    @PutMapping("/api/content/rooms/{roomId}/increment-joined-users")
    void incrementJoinedUsers(@PathVariable("roomId") Long roomId);

    @PutMapping("/api/content/rooms/{roomId}/decrement-joined-users")
    void decrementJoinedUsers(@PathVariable("roomId") Long roomId);

    @GetMapping("/api/content/challenges/{id}/details")
    ChallengeWithSolutionDTO getChallengeWithSolutionById(@PathVariable("id") Long id);
}
