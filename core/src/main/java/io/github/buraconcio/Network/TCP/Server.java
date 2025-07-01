package io.github.buraconcio.Network.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.buraconcio.Utils.Common.Constants;

public class Server {

    private ServerSocket serverSocket;

    public volatile static boolean flagAccept = true;

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public void stopAccepting() {
        Server.flagAccept = false;
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
            serverSocket = new ServerSocket(Constants.PORT);
            System.out.println("server TCP CRIADO");

            flagAccept = true;
            
            while (flagAccept) {
                Socket socket = serverSocket.accept();

                System.out.println("Server connected");

                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                if (clients.size() > 4) {
                    stopAccepting();
                }
            }

        } catch (IOException e) {

            // System.out.println("socket TCP fail");

            if ("Socket closed".equals(e.getMessage())) {

                System.err.println("connection successfully closed");

            } else if (e.getMessage() != null && e.getMessage().toLowerCase().contains("address already in use")) {

                Constants.localP().setHosting(false);
                System.out.println("entering as client");

            } else {

                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Message msg = new Message(Message.Type.DISCONNECT, "get out");

                for (ClientHandler client : clients) {
                    client.broadcastMessage(msg);
                }

                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeButton(boolean x) {
        String str = x ? "tostart" : "tocancel";
        sendString(Message.Type.SERVER_NOTIFICATION, str);
    }

    public void sendString(Message.Type type, String str) {
        Message msg = new Message(type, str);

        for (ClientHandler client : clients) {
            client.broadcastMessage(msg);
        }
    }

    public void sendArray(Message.Type type, ArrayList<String> list){
        Message msg = new Message(type, list);

        for(ClientHandler client : clients){
            client.broadcastMessage(msg);
        }
    }

    public void sendMessage(Message.Type type, Object payload) {
        Message msg = new Message(type, payload);

        for(ClientHandler client : clients){
            client.broadcastMessage(msg);
        }
    }
}
