package models.people;

import lombok.Getter;
import models.organization.Course;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return weeklyHours == teacher.weeklyHours && competentIn == teacher.competentIn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weeklyHours, competentIn);
    }

    public boolean canTeach(Course course){
        return competentIn.equals(course);
    }
}
