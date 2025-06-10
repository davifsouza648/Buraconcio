package io.github.buraconcio.Network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.UdpPackage;

public class UDPServer {

    private DatagramSocket socket;
    private final byte[] receiveData = new byte[1024];

    private final ConcurrentHashMap<Integer, UdpPackage> packageList = new ConcurrentHashMap<>();
    private final List<InetSocketAddress> clientAddressList = new CopyOnWriteArrayList<>();
    private boolean run = ConnectionManager.getInstance().getUDPRun();

    public void startUDPServer() {
        Thread thread = new Thread(this::runUDPServer);
        thread.setDaemon(true);
        thread.start();
    }

    public void runUDPServer() {
        try {
            socket = new DatagramSocket(Constants.UDP_PORT_SERVER);

            System.out.println("Porta " + Constants.UDP_PORT_SERVER + " aberta.");

            new Thread(() -> {
                while (run) {

                    broadcastPlayersPackages();

                    packageList.clear();

                    try {
                        Thread.sleep(16);

                    } catch (InterruptedException e) {

                        e.printStackTrace();

                    }
                }
            }).start();

            while (run) {

                receivePackage();

            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro no servidor UDP: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private UdpPackage deserialize(byte[] data) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        Object obj = in.readObject();
        in.close();

        if (obj instanceof UdpPackage) {
            return (UdpPackage) obj;

        } else {

            System.out.println("ERRO NO PACKAGE");
            return null;

        }
    }

    private void receivePackage() throws IOException, ClassNotFoundException {
        DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivedPacket);

        UdpPackage pacote = deserialize(receivedPacket.getData());

        InetSocketAddress clientAddress = new InetSocketAddress(receivedPacket.getAddress(), receivedPacket.getPort());

        if (!clientAddressList.contains(clientAddress)) {
            clientAddressList.add(clientAddress);
        }

        if (pacote != null) {
            packageList.put(pacote.getId(), pacote);
        }
    }

    private byte[] serializeList(List<UdpPackage> packages) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(packages);
        out.flush();

        return bos.toByteArray();
    }

    private void broadcastPlayersPackages() {
        try {
            List<UdpPackage> packagesToSend = new ArrayList<>(packageList.values());
            byte[] sendData = serializeList(packagesToSend);

            for (InetSocketAddress s : clientAddressList) {

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, s.getAddress(),
                        s.getPort());
                socket.send(sendPacket);

            }

        } catch (IOException e) {

            System.out.println("Erro no envio: " + e.getMessage());

        }
    }

}
