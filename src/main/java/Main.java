import models.people.Student;
import models.rooms.*;
import utils.Generator;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static ArrayList<Room> mockRooms(int count){
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            availableRooms.add(new Room());
        }

        return availableRooms;
    }

    public static ArrayList<Student> mockStudents(int count){
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String firstName = Generator.generateName();
            String lastName = Generator.generateLastName();

            students.add(new Student(firstName, lastName));
        }
        return students;
    }

    public static void main(String[] args){
        List<Room> availableRooms = mockRooms(10);
        List<Student> students = mockStudents(100);
    }
}
