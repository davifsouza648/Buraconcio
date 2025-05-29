package io.github.buraconcio.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Texture;

public class Button {

    public ImageButton createButton(String pathBase, String nomeBase) {
        String skinPath = "buttons/" + pathBase + "/" + nomeBase.toLowerCase() + ".json";
        String atlasPath = "buttons/" + pathBase + "/" + nomeBase.toLowerCase() + ".atlas";

        Skin skin = new Skin(Gdx.files.internal(skinPath));
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        
        atlas.getTextures().forEach(texture -> {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        });

        skin.addRegions(atlas);

        ImageButtonStyle style = new ImageButtonStyle();
        nomeBase = nomeBase.toUpperCase();
        style.up = skin.getDrawable(nomeBase + "1");
        style.down = skin.getDrawable(nomeBase + "2");
        style.over = skin.getDrawable(nomeBase + "3");

        return new ImageButton(style);
    }
}
