package hr.rassus.dz1.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserAdmin extends Remote {

    String RMI_NAME = "//localhost/Registry";

    boolean register(String username, String sharedFiles, String address, int port) throws RemoteException;

    String searchFile(String filename) throws RemoteException;

    UserAddress searchUser(String username) throws RemoteException;
}
