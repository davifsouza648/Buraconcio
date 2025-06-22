package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.UI.Button;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class CreditsScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skinLabel, skinTextField;
    private Animation<TextureRegion> backAnimation;
    private float elapsedTimeBack = 0f;
    private Image backImage;
    private Image fotoImage, titleImage;
    private ImageButton backButtonImage;

    public CreditsScreen(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        CursorManager.resetToArrow();

        backAnimation = Auxiliaries.animationFromFiles("backgrounds/backgroundMenu.png", "backgrounds/backgroundMenu.json");
        backImage = new Image(new TextureRegionDrawable(backAnimation.getKeyFrame(0)));

        // Botão back
        Button back = new Button();
        backButtonImage = back.createButton("back", "backc");

        backButtonImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Auxiliaries.clearAddLocal();
                game.setScreen(new MainMenu(game));
                SoundManager.getInstance().playSound("buttonClick");
                dispose();
            }
        });

        Texture titleTextute = new Texture(Gdx.files.internal("credits/developers.png"));
        titleImage = new Image(titleTextute);

        // Texture texture = new Texture(Gdx.files.internal("credits/teste.jpg"));
        Texture texture = new Texture(Gdx.files.internal("credits/time.jpg"));
        fotoImage = new Image(texture);

        float originalWidth = texture.getWidth();
        float originalHeight = texture.getHeight();

        float maxWidth = 800;
        float maxHeight = 600;

        float scale = Math.min(maxWidth / originalWidth, maxHeight / originalHeight);

        fotoImage.setSize(originalWidth * scale, originalHeight * scale);

        Label nomes = new Label("Arthur, Davi, Mario e Murilo", skinLabel, "labelPixelyWhite64");
        nomes.setFontScale(1f);

        Table backgroundTable = new Table();
        backgroundTable.setFillParent(true);
        backgroundTable.add(backImage).expand().fill();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(titleImage).padBottom(20).size(800, 120).padTop(10);
        table.row();
        table.add(fotoImage).size(fotoImage.getWidth(), fotoImage.getHeight()).padBottom(20);
        table.row();
        table.add(createNames()).padBottom(26);
        table.row();
        table.add(backButtonImage).pad(10).size(260, 104);

        table.setDebug(false);


        stage.addActor(backgroundTable);
        stage.addActor(table);
    }

    public Table createNames(){

    String[] nomes = {"Arthur", "Davi", "Mario", "Murilo"};
    Table row = new Table();

    for(String str : nomes){
        TextField nameField = new TextField(str, skinTextField, "labelPixelyWhite32");
        nameField.setAlignment(Align.center);
        nameField.setDisabled(true);

        row.add(nameField).width(200).padRight(10);
    }

    return row;
}

    @Override
    public void show() {
        CursorManager.resetToArrow();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        elapsedTimeBack += delta * 0.7f;

        backImage.setDrawable(new TextureRegionDrawable(backAnimation.getKeyFrame(elapsedTimeBack)));

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skinLabel.dispose();
    }
}
