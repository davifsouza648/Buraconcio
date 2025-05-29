package io.github.buraconcio.Network;

import io.github.buraconcio.Objects.Player;


public interface ServerListener {

    void PlayerCon(Player player);
    void PlayerDis(Player player);

}
