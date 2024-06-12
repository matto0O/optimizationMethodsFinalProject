package models.people;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Person implements Serializable {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
