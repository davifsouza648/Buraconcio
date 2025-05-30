package io.github.buraconcio.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Controle de flag e lista de clientes você precisa receber por algum jeito
    private volatile boolean flagAccept;
    private List<ClientHandler> clients; // lista compartilhada do servidor

    public ClientHandler(Socket socket, boolean flagAccept, List<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
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
                sendPlayers(out);

                Thread.sleep(100);
            }

            if (!socket.isClosed()) {
                out.writeObject(false);
                out.flush();
            }

            while (!socket.isClosed()) {
                out.writeObject("Modo de envio alternativo ativo");
                out.flush();

                Thread.sleep(1000);
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.out.println("Client disconnected or error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public void broadcastPlayerList() {
        List<Player> players = PlayerManager.getInstance().getAllPlayers();
        for (ClientHandler client : clients) {
            try {

                client.out.writeObject(players);
                client.out.flush();

            } catch (IOException e) {

                System.out.println("Erro ao enviar lista: " + e.getMessage());

            }
        }
    }


    private void sendPlayers(ObjectOutputStream out) throws IOException {
        List<Player> players = PlayerManager.getInstance().getAllPlayers();
        out.writeObject(players);
        out.flush();
        System.out.println("Lista enviada.");
    }

    private void receivePlayer(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Player) {
            Player newPlayer = (Player) obj;
            PlayerManager.getInstance().addPlayer(newPlayer);

            broadcastPlayerList();
            System.out.println("Novo player: " + newPlayer.getUsername());
        } else {
            System.out.println("Not a player");
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

        clients.remove(this);
        System.out.println("ClientHandler stopped and removed");
    }
}
