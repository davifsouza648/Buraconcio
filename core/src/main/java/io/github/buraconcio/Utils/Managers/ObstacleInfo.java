package io.github.buraconcio.Utils.Managers;

import java.io.Serializable;

public class ObstacleInfo implements Serializable {
    public String obstacleName;
    public float obstaclePosX;
    public float obstaclePosY;

    public ObstacleInfo() {}

    public ObstacleInfo(String name, float x, float y) {
        this.obstacleName = name;
        this.obstaclePosX = x;
        this.obstaclePosY = y;
    }
}
