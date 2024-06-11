package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.rooms.Room;

@Getter
@AllArgsConstructor
public class Course {
    private String name;
    private int maxStudents;
    private Class<Room> roomType;

    @Override
    public String toString() {
        return name + " (" + maxStudents + " students)" + " in " + roomType.getSimpleName();
    }
}
