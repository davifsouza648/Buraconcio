package io.github.buraconcio.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.badlogic.gdx.utils.Json;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.PlayerManager;

public class Client {

    private static final String IP = "localhost";
    private static final int PORT = 5050;
    private ServerListener listener;
    private boolean svScreen = true;

    public void startTCPClient() {
        Thread thread = new Thread(() -> connect());
        thread.setDaemon(true);
        thread.start();
    }

    public void connect() {
        try (Socket socket = new Socket(IP, PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String serverMsg = (String) in.readObject();
            System.out.println("receive: " + serverMsg);
            out.flush();

            sendLocalPlayer(out);

            receivePlayerList(in);

        } catch (Exception e) {
            System.err.println("connection fail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendLocalPlayer(ObjectOutputStream out) throws IOException {
        Player p = PlayerManager.getInstance().getLocalPlayer();
        out.writeObject(p);
        out.flush();
    }

    public void receivePlayerList(ObjectInputStream in) throws IOException, ClassNotFoundException {

        while (svScreen) {
            Object obj = in.readObject();

            if (obj instanceof Boolean) {

                Boolean msg = (Boolean) obj;

                if (msg == false) {

                    svScreen = false;

                }

            } else if (obj instanceof List<?>) {

                @SuppressWarnings("unchecked")
                List<Player> players = (List<Player>) obj;
                PlayerManager.getInstance().setPlayers(players);

                if (listener != null) {
                    listener.PlayerCon();
                }

            } else {

                System.out.println("objeto rrecebido: " + obj.getClass());
            }
        }
    }

    public interface ServerListener { //puxar o refresh
        void PlayerCon();
    }

    public void setPlayerConnectedListener(ServerListener listener) {
        this.listener = listener;
    }

}
