package io.github.buraconcio.Network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Server {

    private static final int PORT = 5050;

    private ServerSocket serverSocket;

    private volatile boolean flagAccept = true;

    private final List<ClientHandler> clients = new ArrayList<>();

    public void stopAccepting() {
        this.flagAccept = false;
    }

    public void startTCPServer() {
        Thread thread = new Thread(() -> runTCPServer());
        thread.setDaemon(true);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));

    }

    private void runTCPServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("server TCP CRIADO");

            while (flagAccept) {
                Socket socket = serverSocket.accept();

                System.out.println("Server connected");

                ClientHandler clientHandler = new ClientHandler(socket, flagAccept, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                if (clients.size() >= 4) {
                    stopAccepting();
                }
            }

        } catch (IOException e) {
            System.out.println("socket TCP fail");
            e.printStackTrace();
        }
    }

    public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
