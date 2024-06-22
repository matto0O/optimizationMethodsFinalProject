package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import models.people.Teacher;
import models.rooms.Room;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Lesson implements Serializable {
    private Course course;
    private Room room;
    private Teacher teacher;
    private SchoolDateTime schoolDateTime;

    @Override
    public String toString() {
        return course.name() + " in room no." + room.getRoomNumber() + " on " + schoolDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return course == lesson.course && Objects.equals(room, lesson.room) && Objects.equals(teacher, lesson.teacher) && Objects.equals(schoolDateTime, lesson.schoolDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, room, teacher, schoolDateTime);
    }
}
