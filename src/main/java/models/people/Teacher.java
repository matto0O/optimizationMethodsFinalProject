package models.people;

import lombok.Getter;
import models.organization.Course;
import models.organization.SchoolDay;

import java.util.List;

@Getter
public class Teacher extends Person {
    private final int weeklyHours;
    private final List<SchoolDay> inavailabilityList;
    private final List<Course> competentIn;

    public Teacher(String name, String surname, int weeklyHours,
                   List<SchoolDay> inavailabilityList, List<Course> competentIn) {
        super(name, surname);
        this.weeklyHours = weeklyHours;
        this.inavailabilityList = inavailabilityList;
        this.competentIn = competentIn;
    }

    @Override
    public String toString() {
        return super.toString() + " - teacher (" + weeklyHours + " hours per week)";
    }
}
