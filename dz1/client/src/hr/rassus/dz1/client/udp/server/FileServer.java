package hr.rassus.dz1.client.udp.server;

import hr.rassus.dz1.client.udp.server.network.SimpleSimulatedDatagramSocket;

import java.io.*;
import java.net.*;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServer extends Thread implements ServerIf {

    private int port;
    private SimpleSimulatedDatagramSocket serverSocket;
    private boolean runningFlag = false;


    public FileServer(int portNo) {

        super("FileServer.MainThread");

        port = portNo;
    }

    // Starts all required server services.
    public void startup() {
        try {
            serverSocket = new SimpleSimulatedDatagramSocket(port, 0.2, 200);

            runningFlag = true;

        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
            System.exit(-1);
        }
    }

    // The main loop for accepting client requests.
    public void loop() {

        while (runningFlag) {
            try {
                serverSocket.setSoTimeout(500);

                Paket request = new Paket();
                DatagramPacket request_packet = new DatagramPacket(request.data, request.data.length);

                serverSocket.receive(request_packet);

                String filename = new String(request.getPaketContent());

                System.err.println("Requested " + filename + " from " + request_packet.getAddress().toString() + ":" + Integer.toString(request_packet.getPort()));

                // prepare packets for sending
                LinkedList<Paket> paketi = new LinkedList<Paket>();
                byte[] buffer = new byte[Paket.ContentSize];

                File fileIn = new File(filename);
                FileInputStream fos = new FileInputStream(fileIn);
                while ((fos.read(buffer)) > 0) {
                    Paket paket_x = new Paket();

                    paket_x.setPaketNumber(paketi.size());
                    paket_x.setPaketContent(buffer);

                    paketi.add(paket_x);

                    Arrays.fill(buffer, (byte) 0);
                }
                fos.close();

                serverSocket.setSoTimeout(5000);
                boolean sentThemAll = false;

                while (sentThemAll == false) {
                    sentThemAll = true;

                    for (Paket X: paketi) {
                        if (X.isReceived() == false) {
                            DatagramPacket paket = new DatagramPacket(X.data, X.data.length, request_packet.getAddress(), request_packet.getPort());

                            System.out.println("Sending packet " + X.getPaketNumber());

                            serverSocket.send(paket);
                            sentThemAll = false;
                        }
                    }

                    while (sentThemAll == false) {
                        try {
                            Paket response = new Paket();
                            DatagramPacket response_packet = new DatagramPacket(response.data, response.data.length);

                            serverSocket.receive(response_packet);

                            int packet_number = response.getPaketNumber();
                            System.out.println("Received ack for " + Integer.toString(packet_number));

                            Paket X = paketi.get(packet_number);
                            X.setReceived();
                            paketi.set(packet_number, X);

                        } catch (SocketTimeoutException e) {
                            break;
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

                System.out.println("Received ack for all packets");

            } catch (SocketTimeoutException ste) {
                // wait for next request
            } catch (AccessControlException e) {
                System.out.println("Access control exception " + e.getMessage());
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
                System.exit(-1);
            }
        }
    }

    public void shutdown() {
        System.out.println("File server shutdown.");
        if (serverSocket != null) {
            serverSocket.close();
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

    public static FileServer startServer(int port) {
        FileServer UDPServer = new FileServer(port);
        new Thread(UDPServer).start();
        return UDPServer;
    }

    @Override
    public void run () {
        this.startup();

        this.loop();

        this.shutdown();
    }
}
