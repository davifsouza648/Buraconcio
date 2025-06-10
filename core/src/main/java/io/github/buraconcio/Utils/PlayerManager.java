package io.github.buraconcio.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.Vector2;

import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.UdpPackage;

// singleton
public class PlayerManager {
    private List<Player> players = new CopyOnWriteArrayList<>();
    private static PlayerManager instance;
    private int localPlayerID;
    private Player localPlayer;

    public PlayerManager() {
        players = new ArrayList<>();
    }

    public static synchronized PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }

        return instance;
    }

    public void addPlayer(Player player) {
        removePlayerbyId(player.getId());

        players.add(player);
    }

    public void removePlayerbyId(int id) {
        Iterator<Player> it = players.iterator();

        while(it.hasNext()) {
            Player p = it.next();

            if (p.getId() == id) {
                p.dispose();
                it.remove();
            }
        }
    }

    public void removePlayerbyUser(String name) {
        Iterator<Player> it = players.iterator();

        while(it.hasNext()) {
            Player p = it.next();

            if (p.getUsername() == name) {
                p.dispose();
                it.remove();
            }
        }
    }

    public Player getPlayer(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public boolean exists(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getAllPlayers() {
        return List.copyOf(players);
    }

    public void setPlayers(List<Player> newPlayers) {
        for (Player player : players) {
            player.dispose();
        }

        players.clear();
        players.addAll(newPlayers);
    }

    public void updatePlayers(List<UdpPackage> update) {
        Runnable task = () -> {

            for (UdpPackage pack : update) { //modificar para comparar por PackType do UDPpackage e atualizar oq realmente importa

                // testing ball for now
                int playerId = pack.getId();

                if (playerId != Constants.localP().getId()) {

                    Vector2 ballPos = new Vector2(pack.getBallX(), pack.getBallY());
                    Vector2 ballVel = new Vector2(pack.getBallVX(), pack.getBallVY());

                    Vector2 obstaclePos = new Vector2(pack.getObsX(), pack.getObsY());

                    PlayerManager.getInstance().getPlayer(playerId).update(ballPos, ballVel, obstaclePos);

                }
            }

        };
        PhysicsManager.getInstance().schedule(task);
    }



    public int getPlayerCount() {
        return players.size();
    }

    public void setLocalPlayer(Player player) {

        this.localPlayer = player;
        addPlayer(player);

    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public void clear() {
        players.clear();
    }
}
