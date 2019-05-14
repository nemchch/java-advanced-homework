package ru.ifmo.rain.nemchunovich.bank;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Person extends Remote, Serializable {
    String getName() throws RemoteException;

    String getSurname() throws RemoteException;

    Account addAccount(String accountId) throws RemoteException;

    Account getAccount(String accountId) throws RemoteException;

}
