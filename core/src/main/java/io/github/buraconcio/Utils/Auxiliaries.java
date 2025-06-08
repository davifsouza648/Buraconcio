package io.github.buraconcio.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import io.github.buraconcio.Screens.MainMenu;

public class Auxiliaries {

    // limpar o terminal para debug
    public final static void cls() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            } else {

                System.out.print("\033[H\033[2J");
                System.out.flush();

            }
        } catch (final Exception e) {
            System.out.println("erro ao limpar terminal" + e.getMessage());
        }
    }

    public final static void clearAddLocal(){
        PlayerManager.getInstance().clear();
        PlayerManager.getInstance().addPlayer(PlayerManager.getInstance().getLocalPlayer());
    }

    public static final Animation<TextureRegion> animationFromFiles(String imagePath, String jsonPath) {
        Texture spriteSheet = new Texture(Gdx.files.internal(imagePath));
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal(jsonPath));

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (JsonValue frame : root.get("frames")) {
            JsonValue frameData = frame.get("frame");

            int x = frameData.getInt("x");
            int y = frameData.getInt("y");
            int w = frameData.getInt("w");
            int h = frameData.getInt("h");

            frames.add(new TextureRegion(spriteSheet, x, y, w, h));
        }

        return new Animation<TextureRegion>(0.1f, frames, Animation.PlayMode.LOOP);

    }
}
