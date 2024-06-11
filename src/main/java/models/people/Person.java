package models.people;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Person {
    private static int count = 0;
    private final int id;
    private final String name, surname;

    public Person(String name, String surname) {
        this.id = ++count;
        this.name = name;
        this.surname = surname;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
