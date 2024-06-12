package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@AllArgsConstructor
public class SchoolClass implements Serializable {
    private String name;
    // Course -> number of hours per week
    private Map<Course, Integer> courses;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SchoolClass) {
            return ((SchoolClass) obj).name.equals(this.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
