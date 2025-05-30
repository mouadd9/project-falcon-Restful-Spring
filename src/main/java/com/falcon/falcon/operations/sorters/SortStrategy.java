package com.falcon.falcon.operations.sorters;

import com.falcon.falcon.operations.RoomOperation;

/**
 * Strategy interface for room sorting operations.
 * Extends RoomOperation to participate in the Composite pattern.
 */
public interface SortStrategy extends RoomOperation {
    // Inherits apply(List<RoomDTO> rooms) from RoomOperation
}