package com.falcon.falcon.facadePattern;

import com.falcon.falcon.dto.RoomDTO;

import java.util.List;

// ready for tests !!
// To show personalized room cards, we need to create a facade method that combines room and user-specific data.
public interface DomainFacade {
    List<RoomDTO> getRoomCatalogForUser(Long userId);
}
