/*
 * Ovaj kod razvijen je na Zavodu za Telekomunikacije za domacu zadacu
 * "Distribuirano programiranje" iz predmeta "Raspodijeljeni sustavi".
 */
package hr.rassus.dz1.client;


import hr.rassus.dz1.client.tcp.server.ChatServer;
import hr.rassus.dz1.client.udp.client.FileClient;
import hr.rassus.dz1.client.udp.server.FileServer;
import hr.rassus.dz1.server.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static final String APP_NAME = "UserAdmin";
    public static final String EXIT_COMMAND = "exit";
    public static final String CHAT_EXIT_COMMAND = "quit";

    private static int getAvailablePort() throws IOException {
        Random RANDOM = new Random();
        int port = 0;
        do {
            port = RANDOM.nextInt(20000) + 10000;
        } while (!isPortAvailable(port));

        return port;
    }

    private static boolean isPortAvailable(final int port) throws IOException {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (final IOException e) {
        } finally {
            if (ss != null) {
                ss.close();
            }
        }

        return false;
    }

    public static void main(String[] args) {
        // if (System.getSecurityManager() == null) {
        //     System.setSecurityManager(new RMISecurityManager());
        // }

        try {
            String cmd;
            String[] tokens;

            final UserAdmin remoteObject = (UserAdmin) Naming.lookup(UserAdmin.RMI_NAME);
            final BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // needed to establish TCP server
            String address = InetAddress.getLocalHost().getHostAddress();
            int PORT = getAvailablePort();

            System.err.println("Welcome to the " + APP_NAME + "! Type \"" + EXIT_COMMAND + "\" to end the session.");
            System.err.println("Enter username and filenames you want to share: ");
            System.err.print(APP_NAME + "$> ");
            System.err.flush();

            // register this client
            cmd = stdIn.readLine();
            tokens = cmd.split("\\ ");

            if (tokens.length > 1) {
                String sharedFiles = new String();

                for (int i = 1; i < tokens.length; ++i) {
                    if (sharedFiles.isEmpty()) {
                        sharedFiles = tokens[i];
                    } else {
                        sharedFiles = sharedFiles + " " + tokens[i];
                    }

                }

                final boolean success = remoteObject.register(tokens[0], sharedFiles, address, PORT);
                if (success) {
                    System.err.println("User " + tokens[0] + " has registered " + sharedFiles + " on " + address + ":" + Integer.toString(PORT));
                } else {
                    System.err.println("Given record already exists!");
                }
            } else {
                System.err.println("You have to enter files you want to share");
                System.exit(1);
            }

            // starting TCP server on address:PORT with filenames
            System.out.println("Creating TCP server on " + address + " " + Integer.toString(PORT));
            ChatServer TCPSserver = ChatServer.startServer(PORT);
            // starting TCP server on address:PORT with filenames
            System.out.println("Creating UDP server on " + address + " " + Integer.toString(PORT));
            FileServer UDPServer = FileServer.startServer(PORT);

            // search user/files
            while (true) {
                System.err.print(APP_NAME + "$> ");
                System.err.flush();

                cmd = stdIn.readLine();
                if (cmd.equals(EXIT_COMMAND)) {
                    System.err.println("End of session.");
                    TCPSserver.setRunningFlag(false);
                    UDPServer.setRunningFlag(false);
                    break;
                }

                try {
                    tokens = cmd.split("\\ ");

                    if ("fetch".equals(tokens[0]) && tokens.length > 3) {
                        String datoteka = tokens[1];
                        String adresa = tokens[2];
                        int port = Integer.parseInt(tokens[3]);

                        // FileClient transfer datoteke s adresa:port
                        FileClient UDPClient = FileClient.startClient(datoteka, adresa, port);

                    } else if ("chat".equals(tokens[0]) && tokens.length > 2) {
                        String adresa = tokens[1];
                        int port = Integer.parseInt(tokens[2]);

                        Socket socket = new Socket(adresa, port);

                        String sendString;

                        // get the socket's output stream and open a PrintWriter on it
                        PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);

                        while (true) {
                            System.err.print("Chat $> ");
                            System.err.flush();
                            sendString = stdIn.readLine();
                            if (sendString.equals(CHAT_EXIT_COMMAND)) {
                                System.out.println("Terminating TCP connection");
                                break;
                            }
                            // print a String and then terminate the line
                            outToServer.println(sendString);
                        }


                        socket.close();

                    } else if ("searchFile".equals(tokens[0]) && tokens.length > 1) {
                        final String clients = remoteObject.searchFile(tokens[1]);
                        if (clients != null) {
                            System.err.println("Users " + clients + " have file " + tokens[1]);
                        } else {
                            System.err.println("File " + tokens[1] + " does not exist");
                        }
                    } else if ("searchUser".equals(tokens[0]) && tokens.length > 1) {
                        UserAddress server = remoteObject.searchUser(tokens[1]);
                        final String filename = tokens[1];

                        if (server != null) {
                            System.err.println("User " + tokens[1] + " can be reached on " + server.get_address() + " " + Integer.toString(server.get_port()));
                        } else {
                            System.err.println("User " + tokens[1] + " does not exist");
                        }
                    } else {
                        System.err.println("Invalid command. Commands: register, searchFile, searchUser");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing input: " + cmd);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Client.class.getName()).log(Level.WARNING, "Exception: ", e);
        }
    }
}
