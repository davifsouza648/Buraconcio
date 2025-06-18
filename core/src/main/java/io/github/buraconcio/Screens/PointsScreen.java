package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Utils.Constants;
import io.github.buraconcio.Utils.CountdownTimer;
import io.github.buraconcio.Utils.GameManager;
import io.github.buraconcio.Utils.PlayerManager;

public class PointsScreen implements Screen {

    private final Main game;
    private final Stage stage;
    private final Skin skinTextField, skinLabel;

    public PointsScreen(Main game) {

        this.game = game;

        this.stage = new Stage(new ScreenViewport());
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        this.skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
    }

    @Override
    public void show() {

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        for(Player p : PlayerManager.getInstance().getAllPlayers()){

            Label points = new Label("Estrelas do jogador " + p.getUsername() + ": " + p.getStars(), skinLabel, "labelPixelyWhite32");
            root.add(points);
            root.row();

        }

        Timer();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


    public void Timer(){


        CountdownTimer countdown = new CountdownTimer(2, new CountdownTimer.TimerListener(){

            @Override
            public void tick(int remainingSecs) {

            }

            @Override
            public void finish() {

                Gdx.app.postRunnable(() -> game.setScreen(GameManager.getInstance().getPhysicsScreen()));
            }

        });

        countdown.start();
    }

}
