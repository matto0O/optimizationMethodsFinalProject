package models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class SchoolDateTime implements Serializable {
    private SchoolDay day;
    private int period;

    @Override
    public String toString() {
        return day + " at period " + period;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SchoolDateTime other) {
            return this.day.equals(other.day) && this.period == other.period;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, period);
    }
}
