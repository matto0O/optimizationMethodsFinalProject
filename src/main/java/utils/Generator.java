package utils;

import com.github.javafaker.Faker;
import models.organization.Course;
import models.people.Teacher;
import models.rooms.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Generator {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static String generateName() {
        return faker.name().firstName();
    }

    public static String generateLastName() {
        return faker.name().lastName();
    }

    public static int generateMaxStudents() {
        return faker.number().numberBetween(20, 40);
    }

    public static HashMap<Course, Integer> generateCourses(int classesPerWeek) {
        HashMap<Course, Integer> courses = new HashMap<>();
        for (int i = 0; i < classesPerWeek; i++) {
            Course course = Course.values()[random.nextInt(Course.values().length)];
            if (courses.containsKey(course)) {
                courses.put(course, courses.get(course) + 1);
            } else {
                courses.put(course, 1);
            }
        }

        return courses;
    }

    public static ArrayList<Room> generateRooms(int count){
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            availableRooms.add(new Room());
        }

        return availableRooms;
    }

    public static ArrayList<Teacher> generateTeachers(int weeklyHours, int count){
        ArrayList<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Course course = Course.values()[i%Course.values().length];
            teachers.add(new Teacher(generateName(), generateLastName(), weeklyHours, course));
        }

        return teachers;
    }
}
