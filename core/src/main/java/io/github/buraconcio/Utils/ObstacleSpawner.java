package io.github.buraconcio.Utils;

import io.github.buraconcio.Objects.BlackHole;
import io.github.buraconcio.Objects.BoostPad;
import io.github.buraconcio.Objects.CircularSaw;
import io.github.buraconcio.Objects.CrossBow;
import io.github.buraconcio.Objects.Honey;
import io.github.buraconcio.Objects.LMetalBox;
import io.github.buraconcio.Objects.LongMetalBox;
import io.github.buraconcio.Objects.LongWoodBox;
import io.github.buraconcio.Objects.MetalBox;
import io.github.buraconcio.Objects.Mine;
import io.github.buraconcio.Objects.Obstacle;
import io.github.buraconcio.Objects.Star;
import io.github.buraconcio.Objects.Trampoline;
import io.github.buraconcio.Objects.WoodBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import com.badlogic.gdx.math.Vector2;

public class ObstacleSpawner {

    private static final String[] types = { "circularsaw", "blackhole", "boostpad", "crossbow", "honey", "lmetalbox",
            "longmetalbox", "longwoodbox", "metalbox", "mine", "star", "woodbox", "trampoline" };

    private Random random;

    private Map<String, Function<Vector2, Obstacle>> obstacleFactory;


    public ObstacleSpawner() {
        random = new Random();

        obstacleFactory = new HashMap<>();

        obstacleFactory.put("circularsaw", pos -> new CircularSaw(new Vector2(9f, 30f)));
        obstacleFactory.put("blackhole", pos -> new BlackHole(new Vector2(19f, 35f)));
        obstacleFactory.put("boostpad", pos -> new BoostPad(new Vector2(15f, 35f)));
        obstacleFactory.put("crossbow", pos -> new CrossBow(new Vector2(9f, 35f)));
        obstacleFactory.put("honey", pos -> new Honey(new Vector2(20f, 30f)));
        obstacleFactory.put("lmetalbox", pos -> new LMetalBox(new Vector2(27f, 25f)));
        obstacleFactory.put("longmetalbox", pos -> new LongMetalBox(new Vector2(20f, 25f)));
        obstacleFactory.put("longwoodbox", pos -> new LongWoodBox(new Vector2(10f, 25f)));
        obstacleFactory.put("metalbox", pos -> new MetalBox(new Vector2(15f, 25f)));
        obstacleFactory.put("mine", pos -> new Mine(new Vector2(17.5f, 30f)));
        obstacleFactory.put("star", pos -> new Star(new Vector2(12f, 35f)));
        obstacleFactory.put("woodbox", pos -> new WoodBox(new Vector2(5f, 25f)));
        obstacleFactory.put("trampoline", pos -> new Trampoline(new Vector2(15f, 30f)));
    }

    // ObstacleSpawner() {
    //     random = new Random();

    //     obstacleFactory = new HashMap<>();

    //     obstacleFactory.put("circularsaw", pos -> new CircularSaw(pos));
    //     obstacleFactory.put("blackhole", pos -> new BlackHole(pos));
    //     obstacleFactory.put("boostpad", pos -> new BoostPad(pos));
    //     obstacleFactory.put("crossbow", pos -> new CrossBow(pos));
    //     obstacleFactory.put("honey", pos -> new Honey(pos));
    //     obstacleFactory.put("lmetalbox", pos -> new LMetalBox(pos));
    //     obstacleFactory.put("longmetalbox", pos -> new LongMetalBox(pos));
    //     obstacleFactory.put("longwoodbox", pos -> new LongWoodBox(pos));
    //     obstacleFactory.put("metalbox", pos -> new MetalBox(pos));
    //     obstacleFactory.put("mine", pos -> new Mine(pos));
    //     obstacleFactory.put("star", pos -> new Star(pos));
    //     obstacleFactory.put("woodbox", pos -> new WoodBox(pos));
    //     obstacleFactory.put("trampoline", pos -> new Trampoline(pos));

    // }

    private String randomObstacleType() {

        int index = random.nextInt(types.length);

        return types[index];
    }

    public ArrayList<String> selectRandomObstacles(int count) {

        ArrayList<String> selected = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            selected.add(randomObstacleType());
        }

        return selected;
    }

    public Obstacle spawnObstacle(String type, Vector2 pos) {

        Function<Vector2, Obstacle> factory = obstacleFactory.get(type.toLowerCase());

        if (factory != null) {

            return factory.apply(pos);

        } else {
            throw new IllegalArgumentException("unk type: " + type);
        }
    }

}
