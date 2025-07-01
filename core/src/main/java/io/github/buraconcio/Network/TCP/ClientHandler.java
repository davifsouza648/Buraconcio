package io.github.buraconcio.Network.TCP;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Screens.ServerScreen;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private List<ClientHandler> clients;

    private Player currentPlayer;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {

        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.clients = clients;

    }

    @Override
    public void run() {
        try {
            out.writeObject("Client connected");
            out.flush();

            out.writeObject(ServerScreen.mapIndex);
            out.flush();

            while (!socket.isClosed()) {

                Object obj = in.readObject();

                if (obj instanceof Message msg) {

                    switch (msg.getType()) {

                        case PLAYER_UPDATE:
                            if (Server.flagAccept) {
                                handlePlayerUpdate(msg);
                            } else {
                                System.out.println("Ignoring PLAYER_UPDATE");
                            }

                            break;

                        case STARS_UPDATE:
                            if (GameManager.getInstance().getCurrentPhase() == PHASE.SHOW_POINTS) {
                                handleStarsUpdate(msg);
                            } else {
                                System.out.println("Ignoring STARS_UPDATE");
                            }
                            break;
                        case BLUEPRINT_OBSTACLES:

                        default:
                            System.out.println("Unknown message type: " + msg.getType());
                            break;
                    }

                }

            }

        } catch (EOFException eof) {
            // evitar exception ao desconectar
        } catch (IOException | ClassNotFoundException e) {

            if (e instanceof java.net.SocketException && e.getMessage().equals("Connection reset")) {
                System.out.println("Cliente desconectou abruptamente.");
            } else {
                e.printStackTrace();
            }

        } finally {
            //dar cleanup ao entrar em uma tela diferente
                cleanup();
        }
    }

    private void handlePlayerUpdate(Message msg) {
        if (msg.getPayload() instanceof Player newPlayer) {

            this.currentPlayer = newPlayer;

            PhysicsManager.getInstance().placePlayer(newPlayer);
            PlayerManager.getInstance().addPlayer(newPlayer);

            broadcastPlayerList();

            System.out.println("new player connected: " + newPlayer.getUsername());
        } else {
            System.out.println("Invalid payload for PLAYER_UPDATE");
        }
    }

    public void broadcastPlayerList() {
        Message msg = new Message(Message.Type.PLAYER_LIST, PlayerManager.getInstance().getAllPlayers());

        for (ClientHandler client : clients) {
            try {

                client.broadcastMessage(msg);
                client.out.flush();

            } catch (IOException e) {

                System.out.println("Erro ao enviar lista para cliente: "); /* + e.getMessage() */
            }
        }
    }

    public void broadcastMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar msg");
            e.printStackTrace();
        }
    }

    public void broadcastStars() {

        for (ClientHandler client : clients) {

            Map<Integer, Integer> starsMap = new HashMap<>();
            for (Player p : PlayerManager.getInstance().getAllPlayers()) {
                starsMap.put(p.getId(), p.getStars());
            }

            Message msg = new Message(Message.Type.STARS_UPDATE, starsMap);

            client.broadcastMessage(msg);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleStarsUpdate(Message msg) {
        try {
            Map<Integer, Integer> starsMap = (Map<Integer, Integer>) msg.getPayload();

            for (Player p : PlayerManager.getInstance().getAllPlayers()) {
                if (starsMap.containsKey(p.getId())) {
                    p.setStars(starsMap.get(p.getId()));
                }
            }

            Map<Integer, Integer> responseStars = new HashMap<>();
            for (Player p : PlayerManager.getInstance().getAllPlayers()) {
                responseStars.put(p.getId(), p.getStars());
            }

            Message responseMsg = new Message(Message.Type.STARS_UPDATE, responseStars);

            broadcastMessage(responseMsg);

            // System.out.println("Stars received and sent back");

        } catch (ClassCastException e) {
            System.out.println("Invalid payload for STARS_UPDATE: " + e.getMessage());
        }
    }


    private void cleanup() {
        try {

            if (in != null)
                in.close();

            if (out != null)
                out.close();

            if (socket != null && !socket.isClosed())
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (currentPlayer != null) {

            PlayerManager.getInstance().removePlayerbyId(currentPlayer.getId());
            System.out.println("Jogador removido: " + currentPlayer.getUsername());
        }

        clients.remove(this);
        System.out.println("Client successfully removed");

        broadcastPlayerList();
    }
}
