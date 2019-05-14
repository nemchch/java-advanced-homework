package ru.ifmo.rain.nemchunovich.bank;

public class LocalPerson extends AbstractPerson {
    public LocalPerson(String name, String surname, String passport) {
        super(name, surname, passport, 0);
    }

    public LocalPerson(AbstractPerson other) {
        super(other);
    }
}
