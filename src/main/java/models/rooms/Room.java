package models.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Room {
    private static int roomCounter = 0;
    private final int roomNumber;

    public Room() {
        roomCounter++;
        roomNumber = roomCounter;
    }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}
