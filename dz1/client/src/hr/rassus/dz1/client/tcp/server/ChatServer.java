package hr.rassus.dz1.client.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Process;

public class ChatServer extends Thread implements ServerIf {

    private int port;
    private ServerSocket serverSocket;
    private boolean runningFlag = false;
    // The number of active TCP connections.
    private int activeConnections = 0;
    private final static int MAX_CLIENTS = 10;

    public ChatServer(int portNo) {

        super("ChatServer.MainThread");

        port = portNo;
    }

    // Starts all required server services.
    public void startup() {
        try {
            // create server socket, bind it to the specified port
            // and set the max queue length for client requests
            serverSocket = new ServerSocket(port, MAX_CLIENTS);

            // set the running flag to true
            runningFlag = true;

        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
            System.exit(-1);
        }
    }

    // The main loop for accepting client requests.
    public void loop() {
        try { // set timeout to avoid blocking
            serverSocket.setSoTimeout(500);
        } catch (SocketException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
            System.exit(-1);
        }
        while (runningFlag) {
            try {
                // accept requests and create a new Worker object
                // in a new thread, or throw a SocketTimeoutException
                Thread w = (Thread) new Worker(serverSocket.accept());
                w.start();
                activeConnections++;
            } catch (SocketTimeoutException ste) {
                // do nothing, check the runningFlag
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
                System.exit(-1);
            }
        }
    }

    public void shutdown() {
        while (activeConnections > 0) {
            System.out.println("WARNING: There are still active connections");
            try {
                Thread.sleep(5000);
            } catch (java.lang.InterruptedException e) {
            }
        }
        if (activeConnections == 0) {
            System.out.println("Chat server shutdown.");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
                    System.exit(-1);
                }
            }
        }
    }

    // The main method creates a new server object, initiates server
    // startup, calls the loop method,
    // and when the loop terminates invokes the shutdown method.
    @Override
    public boolean getRunningFlag() {
        return this.runningFlag;
    }

    @Override
    public void setRunningFlag(boolean flag) {
        this.runningFlag = flag;

    }

    public static ChatServer startServer(int port) {
        ChatServer TCPServer = new ChatServer(port);
        new Thread(TCPServer).start();
        return TCPServer;
    }

    @Override
    public void run () {
        this.startup();

        this.loop();

        this.shutdown();
    }

    // inner class within ChatServer
    private class Worker extends Thread {
        Socket socket;

        Worker(Socket sckt) {
            super("ChatServer.Worker");
            socket = sckt;
        }

        @Override
        public void run() {
            System.out.println("New TCP connection from " + socket.getRemoteSocketAddress().toString());

            String input = null;
            try {
                BufferedReader inFromClient = new BufferedReader( new InputStreamReader(socket.getInputStream()));

                while ((input = inFromClient.readLine()) != null) {
                    System.out.println("Server received from " + socket.getRemoteSocketAddress().toString() + ":\t" + input);
                    if (input.equals("")) {
                        break;
                    }
                }

                inFromClient.close();

                System.out.println(socket.getRemoteSocketAddress().toString() + " has left");
                socket.close();
                activeConnections--;

            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
            }


        }
    }
}
