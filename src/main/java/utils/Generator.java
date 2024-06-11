package utils;

import com.github.javafaker.Faker;
import models.organization.ClassType;
import models.organization.Course;
import models.people.Gender;
import models.rooms.Room;

import java.util.HashMap;
import java.util.List;
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

    public static HashMap<Course, Integer> generateCourses(
            int baseClassesPerWeek, int specializationClassesPerWeek) {
        HashMap<Course, Integer> courses = new HashMap<>();



        courses.put(Course.MATH, 5);
        courses.put(Course.ENGLISH, 5);

        courses.put(Course.IT, 1);
        courses.put(Course.BIOLOGY, 1);
        courses.put(Course.PHYSICS, 1);
        courses.put(Course.CHEMISTRY, 1);
        courses.put(Course.PE, 2);
        courses.put(Course.HISTORY, 2);
        courses.put(Course.FOREIGN, 3);
        return courses;
    }
}
