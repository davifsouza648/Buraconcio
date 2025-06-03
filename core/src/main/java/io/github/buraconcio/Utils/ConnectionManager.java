package io.github.buraconcio.Utils;

import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.Server;

public class ConnectionManager {
    private static ConnectionManager instance;
    private Client client;
    private Server server;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {

        if (instance == null) {
            instance = new ConnectionManager();
        }

        return instance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
