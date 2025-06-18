package io.github.buraconcio.Network;

import java.io.Serializable;

public class Message implements Serializable {

    public enum Type {
        PHASE_CHANGE,
        PLAYER_UPDATE,
        PLAYER_LIST,
        STARS_UPDATE,
        DISCONNECT,
        MAP_CHANGE,
        SERVER_NOTIFICATION
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
