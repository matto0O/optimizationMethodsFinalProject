package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.people.Student;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class SchoolClass {
    private String name;
    // Course -> number of hours per week
    private Map<Course, Integer> courses;
    private List<Student> students;

    @Override
    public String toString() {
        return name + " (" + students.size() + " students)";
    }
}
