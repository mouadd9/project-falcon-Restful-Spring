package org.falcon.progressionservice.client;

import org.falcon.progressionservice.dto.RoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "content-service")
public interface ContentServiceClient {
    @GetMapping("api/content/rooms")
    List<RoomDTO> getAllRooms();
}
