package io.github.buraconcio.Network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.PlayerManager;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean flagAccept;
    private List<ClientHandler> clients;

    private Player currentPlayer;

    public ClientHandler(Socket socket, boolean flagAccept, List<ClientHandler> clients) throws IOException {
        
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.flagAccept = flagAccept;
        this.clients = clients;
        
    }

    @Override
    public void run() {
        try {
            out.writeObject("Client connected");
            out.flush();

            while (!socket.isClosed() && flagAccept) {

                receivePlayer(in);
                // sendPlayers(out);

                Thread.sleep(100);
            }

            if (!socket.isClosed()) {
                out.writeObject(false);
                out.flush();
            }

            while (!socket.isClosed()) {
                out.writeObject("enviar alguma outra coisa");
                out.flush();
                Thread.sleep(1000);
            }

        } catch (EOFException eof) {
            //evitar exception ao desconectar
        } catch (IOException | ClassNotFoundException | InterruptedException e) {

            if (e instanceof java.net.SocketException && e.getMessage().equals("Connection reset")) {
                System.out.println("Cliente desconectou abruptamente.");
            } else {
                e.printStackTrace();
            }

        } finally {

            cleanup();

        }
    }

    private void receivePlayer(ObjectInputStream in) throws IOException, ClassNotFoundException {

        Object obj = in.readObject();

        if (obj instanceof Player) {
            Player newPlayer = (Player) obj;
            this.currentPlayer = newPlayer;
            PlayerManager.getInstance().addPlayer(newPlayer);
            broadcastPlayerList();

            System.out.println("new player connected: " + newPlayer.getUsername());

        } else {
            System.out.println("not a player");
        }
    }

    // private void sendPlayers(ObjectOutputStream out) throws IOException {
    // List<Player> players = PlayerManager.getInstance().getAllPlayers();
    // out.writeObject(players);
    // out.flush();
    // System.out.println("Lista de jogadores enviada.");
    // }

    public void broadcastPlayerList() {
        for (ClientHandler client : clients) {
            try {
                client.out.writeObject(PlayerManager.getInstance().getAllPlayers());
                client.out.flush();
            } catch (IOException e) {

                System.out.println("Erro ao enviar lista para cliente: "); /* + e.getMessage() */

            }
        }
    }

    public void broadcastString(String x) {

        for (ClientHandler client : clients) {
            try {
                client.out.writeObject(x);
                client.out.flush();
            } catch (IOException e) {
                System.out.println("Erro ao enviar msg de disconnect");
            }
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
