package io.github.buraconcio.Utils.Common;

import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PhysicsManager;

public class ResetSingleton {
    public static void resetAll() {
        PhysicsManager.destroyInstance();
        GameManager.destroyInstance();
        ConnectionManager.destroyInstance();

        System.gc();
    }
}
