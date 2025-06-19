package org.falcon.progressionservice.client;

import org.falcon.progressionservice.client.dto.RoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "content-service")
public interface ContentServiceClient {
    @GetMapping("/api/content/rooms")
    List<RoomDTO> getAllRooms();

    @GetMapping("/api/content/rooms")
    List<RoomDTO> getRoomsByIds(@RequestParam("ids") List<Long> roomIds);

    @GetMapping("/api/content/rooms/{roomId}")
    RoomDTO getRoomById(@PathVariable("roomId") Long roomId);
}
