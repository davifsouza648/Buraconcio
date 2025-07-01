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
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Game.Flag;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Objects.Obstacles.Obstacle;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Common.TrainSpawner;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.FlowManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;
import io.github.buraconcio.Utils.Managers.ObstacleInfo;
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

                        System.out.println("PHASE" + GameManager.getInstance().phase);

                        if (GameManager.getInstance().phase == PHASE.LOBBY) {
                            PlayerManager.getInstance().setPlayers(players);


                            if (listener != null) {
                                listener.PlayerCon();
                            }

                        } else {

                            List<Integer> toRemove = new ArrayList<>();

                            for (Player p : PlayerManager.getInstance().getAllPlayers()) {

                                boolean found = false;

                                for (Player received : players) {
                                    if (p.getId() == received.getId()) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    toRemove.add(p.getId());
                                }
                            }

                            for (int id : toRemove) {
                                PlayerManager.getInstance().removePlayerbyId(id);
                            }

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
                            quitFunction();
                        }
                    }

                    case PHASE_CHANGE -> {
                        String phase = (String) msg.getPayload();

                        GameManager.getInstance().getFlow().onReceivePhaseChange(phase);
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

                            // Map<Integer, Integer> info = Map.of(Constants.localP().getId(),
                            //         Constants.localP().getStars());

                            // Message starsMsg = new Message(Message.Type.STARS_UPDATE, info);
                            // out.writeObject(starsMsg);
                            // out.flush();

                            // Object response = in.readObject();

                            // if (response instanceof Message respMsg && respMsg.getType() == Message.Type.STARS_UPDATE) {

                            //     @SuppressWarnings("unchecked")
                            //     Map<Integer, Integer> starsList = (Map<Integer, Integer>) respMsg.getPayload();

                            //     PlayerManager.getInstance().updateStars(starsList);

                            //     // System.out.println("recebeu stars update");
                            // }

                            PlayerManager.getInstance().updateArrivalOrder();

                            System.out.println("CLIENT POINTS");
                            if (listenerGame != null) {
                                listenerGame.showPoints();
                            }

                        } else if (GameManager.getInstance().getCurrentPhase() == PHASE.SHOW_WIN) {

                            // ConnectionManager.getInstance().setUDPRun(false);
                            Gdx.app.postRunnable(() -> GameManager.getInstance().setupWinPhase());

                            if (listenerGame != null) {
                                listenerGame.showWin();
                            }

                        }
                    }

                    case TIMER_STOP -> {
                        System.out.println("Recebeu TIMER_STOP, finalizando timer...");
                        GameManager.getInstance().getFlow().onReceiveTimerStop();
                    }

                    case SPAWN_OBSTACLES -> {

                        @SuppressWarnings("unchecked")
                        ArrayList<ObstacleInfo> obsArray = (ArrayList<ObstacleInfo>) msg.getPayload();

                        obsArray.forEach(type -> {
                            Gdx.app.postRunnable(() -> {
                                Vector2 vec = new Vector2(type.obstaclePosX, type.obstaclePosY);
                                GameManager.getInstance().spawnObstacle(type.obstacleName, vec);
                            });
                        });

                    }

                    case SPAWN_TRAIN -> {
                        Vector3 info = (Vector3) msg.getPayload();

                        Gdx.app.postRunnable(() -> {
                            TrainSpawner.spawnTrain(new Vector2(info.x, info.y), (int) info.z);
                        });
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

                    case FLAG_POS ->
                    {
                        Vector2 pos = (Vector2) msg.getPayload();
                        Gdx.app.postRunnable(() -> {
                            GameManager.getInstance().setFlag(new Flag(pos, 1f));
                        });
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

    public void quitFunction() {

        try {
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dsListener();

        if (ConnectionManager.getInstance().getUdpClient() != null) {

            Auxiliaries.clearAddLocal();
            PlayerManager.getInstance().syncLocalPlayer();
            Main game = GameManager.getInstance().getPhysicsScreen().getGame();

            ConnectionManager.getInstance().closeUDPS();
            Constants.localP().setHosting(false);

            Gdx.app.postRunnable(() -> {
                game.setScreen(new MainMenu(game));
            });
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
