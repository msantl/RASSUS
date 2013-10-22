package hr.rassus.dz1.client.udp.client;

import hr.rassus.dz1.client.udp.server.network.SimpleSimulatedDatagramSocket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: msantl
 * Date: 10/17/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileClient extends Thread{
    private SimpleSimulatedDatagramSocket socket;
    private String filename;
    private InetAddress address;
    private int port;

    public FileClient(String filename, String address, int port) {
        super("FileClient.Thread");

        try{
            socket = new SimpleSimulatedDatagramSocket(0.2, 200);

            socket.setSoTimeout(10000);

            this.port = port;
            this.address = InetAddress.getByName(address);
            this.filename = filename;

        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }

    public static FileClient startClient(String filename, String address, int port) {
        FileClient UDPClient = new FileClient(filename, address, port);
        new Thread(UDPClient).start();
        return UDPClient;
    }

    @Override
    public void run() {
        Paket request = new Paket();
        request.setPaketContent(filename.getBytes());

        DatagramPacket request_packet = new DatagramPacket(request.data, request.data.length, address, port);

        System.out.println("Requested " + filename + " from " + address.toString() + ":" + Integer.toString(port));

        Map<Integer, byte[]> primljeno = new HashMap<Integer, byte[]>();

        try {
            socket.send(request_packet);

            while (true) {
                Paket fragment = new Paket();
                DatagramPacket fragmet_packet = new DatagramPacket(fragment.data, fragment.data.length);

                try {
                    socket.receive(fragmet_packet);

                    int packet_number = fragment.getPaketNumber();
                    System.out.println("Received packet " + Integer.toString(packet_number));

                    primljeno.put(packet_number, fragment.getPaketContent());

                    Paket confirm = new Paket();
                    confirm.setPaketNumber(packet_number);

                    DatagramPacket confirm_packet = new DatagramPacket(confirm.data, confirm.data.length, address, port);
                    socket.send(confirm_packet);

                    System.out.println("Sent ack for packet " + Integer.toString(packet_number));

                } catch (SocketTimeoutException e) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        socket.close();

        if (primljeno.isEmpty() == true) {
            System.out.println("NO packages received");
            return;
        } else {
            System.out.println("Received all packages");
        }

        try {
            FileOutputStream fos = new FileOutputStream(filename + ".copy");
            List<Integer> dolazak = new ArrayList<Integer>(primljeno.keySet());
            Collections.sort(dolazak);

            for (int id: dolazak) {
                System.out.println("Writing packet " + Integer.toString(id));
                fos.write(primljeno.get(id));
            }

            fos.close();

            System.out.println("New file is written to " + filename + ".copy");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
