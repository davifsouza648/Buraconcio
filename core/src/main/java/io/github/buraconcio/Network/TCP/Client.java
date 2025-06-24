package io.github.buraconcio.Network.TCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.FlowManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Managers.PhysicsManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Screens.MainMenu;
import io.github.buraconcio.Screens.ServerScreen;

public class Client {

    private Socket socket;
    private ServerListener listener;
    private boolean firstTime = true;
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

            principalLoop(in, out);

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

    public void sendLocalPlayer(ObjectOutputStream out) throws IOException {

        Message msg = new Message(Message.Type.PLAYER_UPDATE, PlayerManager.getInstance().getLocalPlayer());
        out.writeObject(msg);
        out.flush();

    }

    public void principalLoop(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {

        while (!socket.isClosed()) {

            Object obj = in.readObject();

            if (obj instanceof Message msg) {

                switch (msg.getType()) {

                    case PLAYER_LIST -> {

                        @SuppressWarnings("unchecked")
                        List<Player> players = (List<Player>) msg.getPayload();

                        PlayerManager.getInstance().setPlayers(players);

                        // System.out.println("ANTES DE ATUALIZAR");
                        if (listener != null) {

                            listener.PlayerCon();
                            // System.out.println("ATUALIZAR");
                        }

                    }

                    case SERVER_NOTIFICATION -> {
                        String payload = (String) msg.getPayload();

                        if (payload.equals("tocancel")) {

                            if (listener != null) {
                                listener.ServerStartMatch();
                            }

                        } else if (payload.equals("tostart")) {

                            if (listener != null) {
                                listener.ServerCancelMatch();
                            }

                        }
                    }

                    case MAP_CHANGE -> {

                        String payload = (String) msg.getPayload();

                        if (listener != null) {
                            listener.ServerChangeMap(payload);
                        }

                    }

                    case DISCONNECT -> {

                        String payload = (String) msg.getPayload();

                        if (payload.equals("get out")) {
                            disconnect();
                            dsListener();

                            if (ConnectionManager.getInstance().getUDPRun()) {

                                ConnectionManager.getInstance().setUDPRun(false);
                                Auxiliaries.clearAddLocal();
                                Main game = GameManager.getInstance().getPhysicsScreen().getGame();

                                //nao apenas no connection manager, setar udpclient close
                                // ConnectionManager.getInstance().closeUDPS(); arrumar isso aqui
                                
                                ConnectionManager.getInstance().setUDPclient(null);
                                ConnectionManager.getInstance().setUDPserver(null);
                                
                                Gdx.app.postRunnable(() -> {
                                    game.setScreen(new MainMenu(game));
                                });
                            }

                        }
                    }

                    case PHASE_CHANGE -> {

                        String phase = (String) msg.getPayload();

                        FlowManager.getInstance().onReceivePhaseChange(phase);
                        GameManager.getInstance().setPhase(phase);

                        if (listenerGame != null) {
                            listenerGame.GameScreen();
                        }

                        if (GameManager.getInstance().getCurrentPhase() == PHASE.SELECT_OBJ) {

                            // TODO: empactar tudo e um mmetodo do gameManager

                            GameManager.getInstance().setupSelectObstaclePhase();

                            if (listenerGame != null) {
                                listenerGame.GameScreen();
                            }

                        } else if (GameManager.getInstance().getCurrentPhase() == PHASE.PLAY) {

                            // TODO: empactar tudo e um mmetodo do gameManager

                            GameManager.getInstance().setupPlayPhase();

                        } else if (GameManager.getInstance().getCurrentPhase() == PHASE.SHOW_POINTS) {

                            Map<Integer, Integer> info = Map.of(Constants.localP().getId(),
                                    Constants.localP().getStars());

                            Message starsMsg = new Message(Message.Type.STARS_UPDATE, info);
                            out.writeObject(starsMsg);
                            out.flush();

                            Object response = in.readObject();

                            if (response instanceof Message respMsg && respMsg.getType() == Message.Type.STARS_UPDATE) {

                                @SuppressWarnings("unchecked")
                                Map<Integer, Integer> starsList = (Map<Integer, Integer>) respMsg.getPayload();

                                PlayerManager.getInstance().updateStars(starsList);

                                // System.out.println("recebeu stars update");
                            }

                            if (listenerGame != null) {
                                listenerGame.showPoints();
                            }

                        } else if (GameManager.getInstance().getCurrentPhase() == PHASE.SHOW_WIN) {

                            // ConnectionManager.getInstance().setUDPRun(false);

                            if (listenerGame != null) {
                                System.out.println("showWINNNN");
                                listenerGame.showWin();
                            }

                        }
                    }

                    case TIMER_STOP -> {
                        System.out.println("Recebeu TIMER_STOP, finalizando timer...");
                        FlowManager.getInstance().onReceiveTimerStop();
                    }

                    case SPAWN_OBSTACLES -> {

                        @SuppressWarnings("unchecked")
                        ArrayList<String> obsArray = (ArrayList<String>) msg.getPayload();

                        for (String type : obsArray)
                            Gdx.app.postRunnable(() -> {
                                GameManager.getInstance().spawnObstacle(type, null);
                            });

                        GameManager.getInstance().moveCamera(new Vector2(10f, 30f)); // temporary
                    }

                    case PLAYERS_START_POS -> {
                        @SuppressWarnings("unchecked")
                        HashMap<Integer, Vector2> startPosById = (HashMap<Integer, Vector2>) msg.getPayload();

                        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
                            Vector2 pos = startPosById.get(p.getId());

                            if (pos != null) {
                                p.setStartingPos(pos);
                                p.teleportToStartingPos();
                            }
                        }
                    }

                    case CLEAR_UNCLAIMED -> {
                        PhysicsManager.getInstance().clearUnclaimedObstacles();
                    }

                    default -> {
                        // Ignorar outros tipos
                    }
                }

            } else {
                System.out.println("Invalid message");
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

        void GameScreen();
    }

    public void setGameListener(GameStageListener listener) {
        this.listenerGame = listener;
    }

}
