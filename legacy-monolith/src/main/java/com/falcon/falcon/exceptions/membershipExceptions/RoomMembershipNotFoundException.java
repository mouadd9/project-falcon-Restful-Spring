package com.falcon.falcon.exceptions.membershipExceptions;

public class RoomMembershipNotFoundException extends RuntimeException {
    public RoomMembershipNotFoundException(String message) {
        super(message);
    }
}
