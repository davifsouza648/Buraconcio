package io.github.buraconcio.Objects;

import com.badlogic.gdx.math.Vector2;

public class LongWoodBox extends WoodBox {
    private static String imagePath = "obstacles/box/wood/box64x32.png";
    private static String jsonPath = "obstacles/box/wood/box64x32.json";

    private static Vector2 size = new Vector2(6f, 3f);

    public LongWoodBox(Vector2 pos) {
        super(pos, size, imagePath, jsonPath);
    }
}
