package com.falcon.falcon.operations;

import com.falcon.falcon.dtos.RoomDTO;
import java.util.List;

/**
 * Component interface for the Composite pattern.
 * All room operations (filters, sorters, and composites) implement this interface.
 */
public interface RoomOperation {
    List<RoomDTO> apply(List<RoomDTO> rooms);
}