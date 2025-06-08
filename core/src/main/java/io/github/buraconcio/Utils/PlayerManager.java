package io.github.buraconcio.Utils;

import java.util.ArrayList;
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
        players.add(player);
    }

    public boolean removePlayerbyId(int id) {
        return players.removeIf(p -> p.getId() == id);
    }

    public boolean removePlayerbyUser(String name) {
        return players.removeIf(p -> p.getUsername() == name);
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
        players.clear();
        players.addAll(newPlayers);
    }

    public void updatePlayers(List<UdpPackage> update) {
        Runnable task = () -> {

        System.out.println("called update players");
        System.out.println();

        for (UdpPackage pack : update) {
            // testing ball for now
            int playerId = pack.getId();
            Vector2 ballPos = new Vector2(pack.getBallX(), pack.getBallY());
            Vector2 ballVel = new Vector2(pack.getBallVX(), pack.getBallVY());

            Vector2 obstaclePos = new Vector2(pack.getObsX(), pack.getObsY());

            PlayerManager.getInstance().getPlayer(playerId).update(ballPos, ballVel, obstaclePos);
        }

        };
        PhysicsManager.getInstance().schedule(task);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void setLocalPlayer(int id) {
        this.localPlayerID = id;
    }

    public Player getLocalPlayer() {
        return getPlayer(localPlayerID);
    }

    public void clear() {
        players.clear();
    }
}
