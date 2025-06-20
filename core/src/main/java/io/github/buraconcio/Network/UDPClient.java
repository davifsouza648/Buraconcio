package io.github.buraconcio.Network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.Vector2;

import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.GameManager;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Utils.UdpPackage;
import io.github.buraconcio.Utils.UdpPackage.PackType;

public class UDPClient {

    private DatagramSocket UDPsocket;
    private UdpPackage teste;
    InetAddress address;

    private boolean run = ConnectionManager.getInstance().getUDPRun(), flag = true;
    private final int id = Constants.localP().getId();

    private final byte[] receivedData = new byte[4096];
    private List<UdpPackage> packageList = new CopyOnWriteArrayList<>();

    public void startUDPClient() {
        Thread thread = new Thread(this::connect);
        thread.setDaemon(true);
        thread.start();
    }

    public void connect() {

        try {

            UDPsocket = new DatagramSocket();
            address = InetAddress.getByName(Constants.IP);

            new Thread(() -> {
                while (run) {
                    receiveData();
                }
            }).start();

            while (run) {

                sendPlayerData();

                Thread.sleep(16);

            }
            // UDPsocket.close();

        } catch (IOException | InterruptedException e) {

            System.err.println("erro na comunicacao: " + e.getMessage());

        } finally {

            if (UDPsocket != null && !UDPsocket.isClosed()) {

                UDPsocket.close();

            }
        }
    }

    public UdpPackage selectPackType() {

        switch (GameManager.getInstance().getCurrentPhase()) {

            case PLAY: {
                return createBallPackage(); // AUMENTAR AS INFORMACÇOES DO PACKAGE DA BOLA
            }
            case SELECT_OBJ: {
                if (Constants.localP().getSelectedObstacle() != null) {
                    return createObstaclePackage();
                }else{
                    return createDefaultPackage();
                }
            }
            default:
                return createDefaultPackage();
        }
    }

    private void sendPlayerData() {
        try {

            teste = selectPackType();

            byte[] data = serialize(teste);

            DatagramPacket datagram = new DatagramPacket(data, data.length, address, Constants.UDP_PORT_SERVER);

            UDPsocket.send(datagram);

        } catch (IOException e) {

            System.err.println("Falha ao enviar pacote UDP: " + e.getMessage());

        }
    }

    private UdpPackage createBallPackage() {

        boolean ballState = Constants.localP().getBall().isAlive();
        Vector2 pos = Constants.localP().getBall().getWorldPosition();
        float x = pos.x;
        float y = pos.y;
        Vector2 velocity = Constants.localP().getBall().getBody().getLinearVelocity();

        return new UdpPackage(id, x, y, velocity, ballState, PackType.BALL);
    }

    private UdpPackage createObstaclePackage() {

        Vector2 pos = Constants.localP().getSelectedObstacle().getWorldPosition();
        float x = pos.x;
        float y = pos.y;
        boolean placed = Constants.localP().hasPlacedObstacle();
        // Vector2 velocity =
        // Constants.localP().getSelectedObstacle().getBody().getLinearVelocity();

        int ObsID;

        if (Constants.localP().getSelectedObstacle() != null) {
             ObsID= Constants.localP().getSelectedObstacle().getId();
             return new UdpPackage(id, x, y, ObsID, PackType.OBSTACLE);
        }else{
            return new UdpPackage(id, placed, PackType.DEFAULT);
        }


    }

    private UdpPackage createDefaultPackage() {
        boolean placed = Constants.localP().hasPlacedObstacle();

        return new UdpPackage(id, placed, PackType.DEFAULT);
    }

    private byte[] serialize(UdpPackage packet) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(packet);
        out.flush();

        return bos.toByteArray();
    }

    private void receiveData() {

        try {
            DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);

            UDPsocket.receive(packet);

            byte[] validData = new byte[packet.getLength()]; // pegando só os dados validos
            System.arraycopy(packet.getData(), 0, validData, 0, packet.getLength());

            packageList = deserialize(validData);

            PlayerManager.getInstance().updatePlayers(packageList);

        } catch (IOException | ClassNotFoundException e) {

            // System.out.println("erro no recebimento: " + e.getMessage());

        }

    }

    @SuppressWarnings("unchecked")
    private List<UdpPackage> deserialize(byte[] data) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);

        Object obj = in.readObject();
        in.close();

        if (obj instanceof List<?>) {
            return (List<UdpPackage>) obj;
        }

        return new ArrayList<>(); // TODO: verificação de se estamos em uma fase de escolha ou jogo;

    }

}
