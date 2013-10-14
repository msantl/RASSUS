package hr.fer.tel.rassus.multithreadserver;

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

public class UpperCaseServer implements ServerIf {

    private int port;
    private ServerSocket serverSocket;
    private boolean runningFlag = false;
    // Reference to a shutdown listener process, it accepts shutdown
    // requests.
    private ShutdownListener shutdownListener = null;
    // The number of active TCP connections.
    private int activeConnections = 0;
    private final static int MAX_CLIENTS = 10;

    public UpperCaseServer(int portNo) {

        port = portNo;
        if (port < 1024) {
            System.err.println("UpperCaseServer: illegal port number [" + port
                    + "]");
            System.exit(-1);
        }
    }

    // Starts all required server services.
    public void startup() {
        try {
            // create server socket, bind it to the specified port
            // and set the max queue length for client requests
            serverSocket = new ServerSocket(port, MAX_CLIENTS);

            // set the running flag to true
            runningFlag = true;

            // create shutdown listener in a new thread
            shutdownListener = new ShutdownListener(this, 4456);
            new Thread(shutdownListener).start();

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
            System.out.println("Server shutdown.");
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

    public static void main(String argv[]) {

        UpperCaseServer server = new UpperCaseServer(10003);

        server.startup();

        server.loop();

        server.shutdown();

        System.exit(0);
    }

    // inner class within UpperCaseServer
    private class Worker extends Thread {

        Socket socket = null;

        Worker(Socket sckt) {
            super("UpperCaseServer.Worker");
            socket = sckt;
        }

        // Creates reader and writer for the socket, reads a line from
        // the reader, transforms it to upper case, and sends the
        // transformed string to client.
        @Override
        public void run() {
            String clientInput = null;
            String capitalizedInput = null;
            try {
                PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                while ((clientInput = inFromClient.readLine()) != null) {
                    System.out.println("Server received from "
                            + socket.getRemoteSocketAddress().toString()
                            + ":\t" + clientInput);
                    if (clientInput.equals("")) {
                        break;
                    }
                    capitalizedInput = clientInput.toUpperCase();
                    outToClient.println(capitalizedInput);
                    System.out.println("Server sends:\t" + capitalizedInput);
                }
                outToClient.close();
                inFromClient.close();
                if (socket != null) {
                    socket.close();
                }
                activeConnections--;
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
            }
        }
    }
}
