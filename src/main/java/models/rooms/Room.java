package models.rooms;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Room implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Room) {
            return ((Room) obj).roomNumber == this.roomNumber;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return roomNumber;
    }
}
