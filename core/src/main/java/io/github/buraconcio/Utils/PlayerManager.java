package io.github.buraconcio.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.buraconcio.Objects.Player;

// singleton
public class PlayerManager {
    private List<Player> players;
    private static PlayerManager instance;
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
        players.add(player);
    }

    public boolean removePlayer(int id) {
        return players.removeIf(p -> p.getId() == id);
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
