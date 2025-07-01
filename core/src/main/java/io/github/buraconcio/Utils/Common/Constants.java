package io.github.buraconcio.Utils.Common;

import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Managers.PlayerManager;

public class Constants {
    public static final float BALL_RADIUS = 0.4f;
    public static final float MAX_IMPULSE = 25f;
    // quanto puxar o mouse pra ter forca maxima
    public static final float MAX_IMPULSE_DISTANCE = 5f;
    // multiplica o tamanho padrao do sensor da flag
    public static final float FLAG_LENIENCY = 0.5f;
    // quanto a bola deve estar parada para poder jogar
    public static final float STILL_TOLERANCE = 0.5f;

    public static final float BOOST_IMPULSE = 20f;

    public static final float MAX_DISTANCE_AUDIBLE = 25f;

    public static final float VELOCITY_HONEY = 1f;

    public static final int POINTS_TO_WIN = 10;

    // usar no server e client
    public static final int PORT = 5050, UDP_PORT_SERVER = 5565, UDP_PORT_CLIENT = 5566;
    public static String IP = "localhost";

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
