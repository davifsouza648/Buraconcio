package io.github.buraconcio.Network;

import java.io.*;
import java.net.*;

import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.UdpPackage;

public class UDPServer {

    private DatagramSocket socket;
    private DatagramPacket receivedPackage, sentPackage;
    private ObjectInputStream in;
    UdpPackage pacoteClient;

    private byte[] receiveData = new byte[1024];

    public void startUDPServer() {
        Thread thread = new Thread(() -> runUDPServer());
        thread.setDaemon(true);
        thread.start();
    }

    public void runUDPServer() {
        try {
            socket = new DatagramSocket(Constants.UDP_PORT);
            System.out.println("Porta: " + Constants.UDP_PORT + "aberta");

            while (true) {

                receivedPackage = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivedPackage);

                ByteArrayInputStream bis = new ByteArrayInputStream(receivedPackage.getData());
                in = new ObjectInputStream(bis);

                pacoteClient = (UdpPackage) in.readObject();

                System.out.println("Pacote recebido do cliente:");
                System.out.println(pacoteClient);

            }

        } catch (IOException | ClassNotFoundException e) {

            System.out.println("error no server UDP " + e.getMessage());
            e.printStackTrace();
        } finally {

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        }

    }

}
