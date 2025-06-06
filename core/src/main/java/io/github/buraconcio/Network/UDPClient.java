package io.github.buraconcio.Network;

import java.io.*;
import java.net.*;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.UdpPackage;

public class UDPClient {

    private DatagramSocket UDPsocket;
    private DatagramPacket PlayerPackage;
    private UdpPackage teste;

    public void startUDPClient() {
        Thread thread = new Thread(() -> connect());
        thread.setDaemon(true);
        thread.start();
    }

    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];

    public void connect() {

        try {

            UDPsocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Constants.IP);

            while (true) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);

                // para teste

                UdpPackage pacote = new UdpPackage(Constants.localP.getId(), Constants.localP.getBall().getX(),
                        Constants.localP.getBall().getY());

                out.writeObject(pacote);
                out.flush();

                sendData = bos.toByteArray();

                PlayerPackage = new DatagramPacket(sendData, sendData.length, address, Constants.UDP_PORT);

                UDPsocket.send(PlayerPackage);
                System.out.println("enviamos o player local");
                System.out.println(pacote);

                Thread.sleep(100);
            }
            // UDPsocket.close();

        } catch (IOException | InterruptedException e) {

            System.out.println("MANDOUU");

        }
    }
}
