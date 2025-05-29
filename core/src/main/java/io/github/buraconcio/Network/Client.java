package io.github.buraconcio.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.PlayerManager;

public class Client {

    private static final String IP = "localhost";
    private static final int PORT = 5050;

    public void connect() {
        try {
            Socket socket = new Socket(IP, PORT);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object message = in.readObject();

            if (message instanceof String) {

                System.out.println("receive: " + message);
            }

            Player p = PlayerManager.getInstance().getLocalPlayer();
            out.writeObject(p);
            out.flush();

            // Object testList = in.readObject();

            // if (testList instanceof List<?>) {

            //     List<?> rawList = (List<?>) testList;
            //     List<Player> players = new ArrayList<>();

            //     for (Object obj : rawList) {
            //         if (obj instanceof Player) {
            //             players.add((Player) obj);
            //         }
            //     }

            //     PlayerManager.getInstance().setPlayers(players);
            // }

            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("connection fail: " + e.getMessage());
            e.getStackTrace();

        }
    }
}
