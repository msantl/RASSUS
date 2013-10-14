package hr.fer.tel.rassus.multithreadserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

//Creates a datagram socket that listens on port 4456 for
//shutdown requests.
public class ShutdownListener implements Runnable {
    // Reference to the server that will be shutdown.

    private ServerIf server = null;
    private DatagramSocket socket = null;
    int port;
    // Running flag: when true the datagram socket is running and
    // accepting packets when false the listener process terminates.
    boolean runningFlag = false;

    public ShutdownListener(ServerIf srv, int portNo) {
        server = srv;
        port = portNo;
        if (port > 1024) {
            try {
                socket = new DatagramSocket(port);
                runningFlag = true;
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);
                System.exit(-1);
            }
        }
    }

    // Listens for datagram packets. When the packet contains the string
    // "Shutdown now" sets the runningFlag of the server to false.
    public void run() {
        byte[] buf = new byte[256];
        String received;
        while (runningFlag) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                received = new String(packet.getData(), packet.getOffset(),
                        packet.getLength());
                if (received.equals("Shutdown now")) {
                    server.setRunningFlag(false);
                    // set my running flag to false
                    runningFlag = false;

                    byte[] sendBuf = "Server is shutting down...".getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendBuf,
                            sendBuf.length, packet.getAddress(), packet.getPort());

                    socket.send(sendPacket);
                }
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception: ", e);            
            }
        }
        socket.close();
    }
}
