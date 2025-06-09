package io.github.buraconcio.Utils;

import io.github.buraconcio.Objects.Player;

public class Constants {
    public static final float BALL_RADIUS = 0.5f;
    public static final float MAX_IMPULSE = 25f;
    // quanto puxar o mouse pra ter forca maxima
    public static final float MAX_IMPULSE_DISTANCE = 5f;
    // multiplica o tamanho padrao do sensor da flag
    public static final float FLAG_LENIENCY = 0.5f;
    // quanto a bola deve estar parada para poder jogar
    public static final float STILL_TOLERANCE = 99999f;

    public static final float BOOST_IMPULSE = 20f;

    // usar no server e client
    public static final int PORT = 5050, UDP_PORT_SERVER = 5565, UDP_PORT_CLIENT = 5566;
    public static String IP = "localhost";

    public static PHASE phase;

    public enum PHASE {
        PLAY,
        SELECT_OBJ,
        SHOW_POINTS,
        SHOW_WIN,
        GET_BACK
    }

    public static void setPhase(String msg) {

        try {

            phase = PHASE.valueOf(msg.toUpperCase());

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

        }

    }

    public static Player localP() {
        return PlayerManager.getInstance().getLocalPlayer();
    }

    public static String getIP() {
        return IP;
    }

    public static boolean isHosting() {
        return localP().getHosting();
    }

    public static void setIP(String newIP) {
        IP = newIP;
    }

    // gambiarra infinita

}
