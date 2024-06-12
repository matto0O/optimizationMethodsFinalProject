package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import models.people.Teacher;
import models.rooms.Room;

@Getter
@Setter
@AllArgsConstructor
public class Lesson {
    private Course course;
    private Room room;
    private Teacher teacher;
    private SchoolDateTime schoolDateTime;

    @Override
    public String toString() {
        return course.name() + " in room no." + room.getRoomNumber() + " on " + schoolDateTime;
    }
}
