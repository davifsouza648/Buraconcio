package io.github.buraconcio.Utils.Common;

import io.github.buraconcio.Objects.Obstacles.*;
import io.github.buraconcio.Utils.Managers.GameManager;

import java.util.*;
import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class ObstacleSpawner {

    private static final String[] types = { "circularsaw", "blackhole", "boostpad", "crossbow", "honey", "lmetalbox",
            "longmetalbox", "longwoodbox", "metalbox", "mine", "star", "woodbox", "trampoline", "eraser" };

    private Random random;
    private Map<String, Function<Vector2, Obstacle>> obstacleFactory;

    public ObstacleSpawner() {
        random = new Random();

        obstacleFactory = new HashMap<>();

        obstacleFactory.put("circularsaw", pos -> new CircularSaw(pos));
        obstacleFactory.put("blackhole", pos -> new BlackHole(pos));
        obstacleFactory.put("boostpad", pos -> new BoostPad(pos));
        obstacleFactory.put("crossbow", pos -> new CrossBow(pos));
        obstacleFactory.put("honey", pos -> new Honey(pos));
        obstacleFactory.put("lmetalbox", pos -> new LMetalBox(pos));
        obstacleFactory.put("longmetalbox", pos -> new LongMetalBox(pos));
        obstacleFactory.put("longwoodbox", pos -> new LongWoodBox(pos));
        obstacleFactory.put("metalbox", pos -> new MetalBox(pos));
        obstacleFactory.put("mine", pos -> new Mine(pos));
        obstacleFactory.put("star", pos -> new Star(pos));
        obstacleFactory.put("woodbox", pos -> new WoodBox(pos));
        obstacleFactory.put("trampoline", pos -> new Trampoline(pos));
        obstacleFactory.put("eraser", pos -> new Eraser(pos));
    }

    public Rectangle getBlueprintArea() {
        Rectangle area = GameManager.getInstance().getBluePrintarea();
        if (area == null) {
            throw new IllegalStateException("Blueprint area is not set in GameManager.");
        }
        return area;
    }

    public Vector2 randomPositionInBlueprint() {

        Rectangle blueprintArea = getBlueprintArea();
        if (blueprintArea == null) {
            throw new IllegalStateException("Blueprint area is not set.");
        }

        float x = (blueprintArea.x + random.nextFloat() * blueprintArea.width) / 32;
        float y = (blueprintArea.y + random.nextFloat() * blueprintArea.height) / 32;

        return new Vector2(x, y);

    }

    public String randomObstacleType() {
        int index = random.nextInt(types.length);
        return types[index];
    }

    public Obstacle spawnRandomObstacle() {
        String type = randomObstacleType();
        Vector2 pos = randomPositionInBlueprint();
        return spawnObstacle(type, pos);
    }

    public Obstacle spawnObstacle(String type, Vector2 pos) {
        Function<Vector2, Obstacle> factory = obstacleFactory.get(type.toLowerCase());
        if (factory != null) {
            return factory.apply(pos);
        } else {
            throw new IllegalArgumentException("Unknown obstacle type: " + type);
        }
    }

    public ArrayList<String> selectRandomObstacles(int count) {
        ArrayList<String> pool = new ArrayList<>(List.of(types));
        ArrayList<String> selected = new ArrayList<>();
        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int index = random.nextInt(pool.size());
            selected.add(pool.remove(index));
        }
        return selected;
    }
}
