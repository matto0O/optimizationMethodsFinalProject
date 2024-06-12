package models.people;

import lombok.Getter;
import models.organization.Course;

import java.io.Serializable;

@Getter
public class Teacher extends Person implements Serializable {
    private final int weeklyHours;
    private final Course competentIn;

    public Teacher(String name, String surname, int weeklyHours, Course competentIn) {
        super(name, surname);
        this.weeklyHours = weeklyHours;
        this.competentIn = competentIn;
    }

    @Override
    public String toString() {
        return super.toString() + " - teacher of " + competentIn.toString() + " (" + weeklyHours + " hours per week)";
    }

    public boolean canTeach(Course course){
        return competentIn.equals(course);
    }
}
