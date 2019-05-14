package ru.ifmo.rain.nemchunovich.bank;

import java.rmi.*;

public interface Bank extends Remote {

    Person createPerson(String name, String surname, String passportId)
            throws RemoteException;


    Person getPerson(String passportId, boolean local)
            throws RemoteException;
}
