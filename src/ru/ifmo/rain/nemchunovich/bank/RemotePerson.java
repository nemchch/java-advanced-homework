package ru.ifmo.rain.nemchunovich.bank;

import java.rmi.Remote;

public class RemotePerson extends AbstractPerson implements Remote {

    public RemotePerson(String name, String surname, String passport, int port) {
        super(name, surname, passport, port);
    }

}
