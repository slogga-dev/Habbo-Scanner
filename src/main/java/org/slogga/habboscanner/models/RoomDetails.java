package org.slogga.habboscanner.models;

import lombok.Data;

@Data
public class RoomDetails {
    private int guestRoomId;
    private String roomName;
    private int ownerId;
    private String currentOwnerName;
    private RoomAccessMode roomAccessMode;

    public RoomDetails(int guestRoomId, String roomName, int ownerId, String currentOwnerName, RoomAccessMode roomAccessMode) {
        this.guestRoomId = guestRoomId;
        this.roomName = roomName;
        this.ownerId = ownerId;
        this.currentOwnerName = currentOwnerName;
        this.roomAccessMode = roomAccessMode;
    }
}
