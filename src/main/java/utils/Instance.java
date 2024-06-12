package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.organization.SchoolClass;
import models.people.Teacher;
import models.rooms.Room;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Instance implements Serializable {
    private List<Room> availableRooms;
    private List<SchoolClass> schoolClasses;
    private List<Teacher> teachers;
    // weekly lessons for every class
    private int lessonsPerWeek;
    // All available timeslots in a day
    private int periods;

    public Instance(List<Room> availableRooms, List<SchoolClass> schoolClasses, List<Teacher> teachers) {
        this.availableRooms = availableRooms;
        this.schoolClasses = schoolClasses;
        this.teachers = teachers;
        // lessons per week for a class
        this.lessonsPerWeek = 30;
        this.periods = 10;
    }

    public Instance() {
        // TODO generators
        this.availableRooms = Generator.generateRooms(100);
        this.schoolClasses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            schoolClasses.add(new SchoolClass((i+1) + "A", Generator.generateCourses(30)));
        }
        this.teachers = Generator.generateTeachers(40, 50);
        this.lessonsPerWeek = 30;
        this.periods = 12;
    }

    public Instance(String filename){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            Instance instance = (Instance) ois.readObject();
            this.availableRooms = instance.getAvailableRooms();
            this.schoolClasses = instance.getSchoolClasses();
            this.teachers = instance.getTeachers();
            this.lessonsPerWeek = instance.getLessonsPerWeek();
            this.periods = instance.getPeriods();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exportInstance(String filename) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(this);
        oos.close();
    }
}
