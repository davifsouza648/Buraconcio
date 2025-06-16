package io.github.buraconcio.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.PlayerManager;
import io.github.buraconcio.Screens.ServerScreen;

public class Client {

    private Socket socket;
    private ServerListener listener;
    private boolean svScreen = true, gameScreen = true;
    private GameStageListener listenerGame;

    public void startTCPClient() {
        Thread thread = new Thread(() -> connect());
        thread.setDaemon(true);
        thread.start();
    }

    public void connect() {
        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress(Constants.IP, Constants.PORT), 5000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String serverMsg = (String) in.readObject();
            System.out.println("receive: " + serverMsg);

            int index = (int) in.readObject();
            ServerScreen.mapIndex = index;

            // server screen
            sendLocalPlayer(out);
            receivePlayerList(in);

            // game updates
            receiveGamePhases(in);

            // receber estagios e outras atualizacoes por tcp

        } catch (IOException | ClassNotFoundException e) {

            dsListener();

            if ("Socket closed".equals(e.getMessage())) {

                System.err.println("connection successfully closed");

            } else {

                // e.printStackTrace();

                System.out.println("connection erro" + e.getMessage());

            }
        }
    }

    private void receiveGamePhases(ObjectInputStream in) throws ClassNotFoundException, IOException {

        while (gameScreen) {

            Object obj = in.readObject();

            if (obj instanceof Boolean) {

                Boolean msg = (Boolean) obj;

                if (!msg) {
                    gameScreen = false;
                }

            } else if (obj instanceof String) {

                String msg = (String) obj;

                Constants.setPhase(msg);

                if (Constants.phase == Constants.PHASE.SELECT_OBJ) {

                    Constants.localP().setCanSelect(true);

                    // decidir se vai ser em uma tela separada

                } else if (Constants.phase == Constants.PHASE.PLAY) {

                    Constants.localP().setCanSelect(false);

                } else if (Constants.phase == Constants.PHASE.SHOW_POINTS) {

                    // atualizar pontuações;

                    if (listener != null) {
                        listenerGame.showPoints();
                    }

                } else if (Constants.phase == Constants.PHASE.SHOW_WIN) {

                    ConnectionManager.getInstance().setUDPRun(false);

                    if (listener != null) {
                        listenerGame.showWin();
                    }

                }

            }

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

                if (!msg) {
                    svScreen = false;
                }

            } else if (obj instanceof List<?>) {

                @SuppressWarnings("unchecked")
                List<Player> players = (List<Player>) obj;
                PlayerManager.getInstance().setPlayers(players);

                if (listener != null) {
                    listener.PlayerCon();
                }
            } else if (obj instanceof String) {
                String msg = (String) obj;

                if (msg.equals("get out")) {

                    disconnect();

                    dsListener();
                    svScreen = false;

                } else if (msg.equals("tocancel")) {

                    if (listener != null) {
                        listener.ServerStartMatch();
                    }

                } else if (msg.equals("tostart")) {
                    if (listener != null) {
                        listener.ServerCancelMatch();
                    }
                } else {
                    if (listener != null) {
                        listener.ServerChangeMap(msg);
                    }
                }
            }
        }
    }

    public void dsListener() {
        if (listener != null) {
            listener.ServerDisconnected();
        }
    }

    public interface ServerListener { // puxar o refresh
        void PlayerCon();

        void ServerDisconnected();

        void ServerStartMatch();

        void ServerCancelMatch();

        void ServerChangeMap(String msg);
    }

    public void setServerListener(ServerListener listener) {
        this.listener = listener;
    }

    public void disconnect() throws IOException {

        if (socket != null && socket.isConnected()) {
            socket.close();
        }

        System.out.println("client disconnect");
    }

    public interface GameStageListener {
        void showWin();

        void showPoints();
    }

    public void setGameListener(GameStageListener listener) {
        this.listenerGame = listener;
    }

}
