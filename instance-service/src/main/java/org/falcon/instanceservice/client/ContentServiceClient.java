package org.falcon.instanceservice.client;

import org.falcon.instanceservice.client.dto.RoomDTO;
import org.falcon.instanceservice.exceptions.CustomContentServiceErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "content-service", configuration = CustomContentServiceErrorDecoder.class)
public interface ContentServiceClient {
    @GetMapping("/api/content/rooms/{roomId}")
    RoomDTO getRoomById(@PathVariable("roomId") Long roomId);
}

