package hr.rassus.dz1.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAdminImpl extends UnicastRemoteObject implements UserAdmin {

    public static final int CLASS_SERVER_PORT = 4727;

    public UserAdminImpl() throws RemoteException {
        super();
    }

    // keep track of users
    Map<String, Set<String>> fileToUsers = new HashMap<String, Set<String>>();
    // keep track of user addresses
    Map<String, UserAddress> userToAddress = new HashMap<String, UserAddress>();

    public boolean register(String username, String sharedFiles, String address, int port) throws RemoteException {
        UserAddress new_address = new UserAddress(port, address);

        if (userToAddress.containsKey(username)) {
            if (new_address.equals(userToAddress.get(username)) == false) {
                // registracija korisnika s druge adrese
                return false;
            }
        } else {
            userToAddress.put(username, new_address);
        }

        String[] files = sharedFiles.split("\\ ");

        for (String file: files) {
            if (fileToUsers.containsKey(file)) {
                if (fileToUsers.get(file).contains(username)) {
                    // ponovni upis iste datoteke
                    return false;
                }
            }
        }

        for (String file: files) {
            if (fileToUsers.containsKey(file)) {
                fileToUsers.get(file).add(username);
            } else {
                Set<String> new_set = new HashSet<String>();
                new_set.add(username);

                fileToUsers.put(file, new_set);
            }
        }


        return true;
    }

    public String searchFile(String filename) throws RemoteException {
        String ret = new String();

        if (fileToUsers.containsKey(filename)) {
            for (String user: fileToUsers.get(filename)) {
                if (ret.isEmpty()) {
                    ret = user;
                } else {
                    ret = ret + " " + user;
                }
            }
        } else {
            return null;
        }

        return ret;
    }

    public UserAddress searchUser(String username) throws RemoteException {
        if (userToAddress.containsKey(username)) {
            return userToAddress.get(username);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }

            System.out.println(
                    "Codebase: " + System.getProperty("java.rmi.server.codebase"));

            // start the class file server
            new ClassServer(CLASS_SERVER_PORT);

            // instantiate and register the remote object
            final UserAdmin serverObject = new UserAdminImpl();
            System.out.println(serverObject);
            Naming.rebind(RMI_NAME, serverObject);
        } catch (MalformedURLException e) {
            System.err.println("RMI Registry service name binding failed due to: " + e);
            System.exit(1);
        } catch (Exception e) {
            Logger.getLogger(UserAdminImpl.class.getName()).log(Level.WARNING, "Exception: ", e);
            System.exit(1);
        }
    }
}
