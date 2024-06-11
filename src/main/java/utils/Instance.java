package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.organization.SchoolClass;
import models.people.Teacher;
import models.rooms.Room;

import java.util.List;

@Getter
@AllArgsConstructor
public class Instance {
    private List<Room> availableRooms;
    private List<SchoolClass> schoolClasses;
    private List<Teacher> teachers;
    private int maxStudentsInClass;
    // student
    private int baseClassesPerWeek;
    private int specializationClassesPerWeek;

    public Instance(List<Room> availableRooms, List<SchoolClass> schoolClasses, List<Teacher> teachers) {
        this.availableRooms = availableRooms;
        this.schoolClasses = schoolClasses;
        this.teachers = teachers;
        this.maxStudentsInClass = 30;
        this.baseClassesPerWeek = 30;
        this.specializationClassesPerWeek = 5;
    }

    public Instance() {
        // TODO generators
        this.availableRooms = null;
        this.schoolClasses = null;
        this.teachers = null;
        this.maxStudentsInClass = 30;
        this.baseClassesPerWeek = 30;
        this.specializationClassesPerWeek = 5;
    }
}
