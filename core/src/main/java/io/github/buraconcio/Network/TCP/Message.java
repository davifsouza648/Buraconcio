package io.github.buraconcio.Network.TCP;

import java.io.Serializable;

public class Message implements Serializable {

    public enum Type {
        PHASE_CHANGE,
        PLAYER_UPDATE,
        PLAYER_LIST,
        STARS_UPDATE,
        DISCONNECT,
        MAP_CHANGE,
        SERVER_NOTIFICATION,
        TIMER_STOP,
        CLEAR_UNCLAIMED,
        SPAWN_OBSTACLES,
        PLAYERS_START_POS,
        BLUEPRINT_OBSTACLES,
        FLAG_POS
    }

    private Type type;
    private Object payload;

    public Message(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
