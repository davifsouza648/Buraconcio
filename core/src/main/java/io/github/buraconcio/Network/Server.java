package io.github.buraconcio.Network;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import com.badlogic.gdx.Gdx;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.PlayerManager;

public class Server {

    private static final int PORT = 5050;
    private boolean start = true;
    private ServerListener listener;
    private boolean flagAccept = true;

    public void stopAccepting() {
        this.flagAccept = false;
        System.out.println("parei");
    }

    public void startTCPServer() {
        new Thread(() -> runTCPServer()).start();
    }

    private void runTCPServer() {
        try (ServerSocket sv = new ServerSocket(PORT)) {

            System.out.println("serverr TCP CRIADO");

            while (flagAccept) {
                Socket socket = sv.accept();

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject("success conection");
                out.flush();

                Object newP = in.readObject();

                if (newP instanceof Player) {

                    Player p = (Player) newP;

                    PlayerManager.getInstance().addPlayer(p);

                    for (Player a : PlayerManager.getInstance().getAllPlayers()) {
                        System.out.println(a);
                    }
                } else {
                    System.out.println("objeto diferente de PLAYER");
                }

                // out.writeObject(PlayerManager.getInstance().getAllPlayers());
                // out.flush();

                socket.close();
            }

        } catch (IOException | ClassNotFoundException  e) {
            System.out.println("socket TCP fail");
            System.out.println("errrrrro");
            e.getStackTrace();
        }
    }

}
