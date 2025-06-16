package com.falcon.falcon.operations.filters;

import com.falcon.falcon.operations.RoomOperation;

/**
 * Strategy interface for room filtering operations.
 * Extends RoomOperation to participate in the Composite pattern.
 */
public interface FilterStrategy extends RoomOperation {
    // Inherits apply(List<RoomDTO> rooms) from RoomOperation
}