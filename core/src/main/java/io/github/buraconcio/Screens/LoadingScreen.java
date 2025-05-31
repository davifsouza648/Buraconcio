package io.github.buraconcio.Screens;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Utils.Constants;

public class LoadingScreen implements Screen {

    private Main game;
    private Stage stage;
    private volatile boolean flag = false;

    public LoadingScreen(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        new Thread(() -> runVerify()).start();

    }

    private void runVerify() {

        for (int i = 0; i < 10; i++) {

            if (isServerAvailable()) {
                flag = true;
                break;
            }

            try {

                Thread.sleep(500);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }

        }

        Gdx.app.postRunnable(() -> {

            if (flag) {

                game.setScreen(new ServerScreen(game));
            } else {
                game.setScreen(new MainMenu(game));

            }
            stage.dispose();
        });

    }

    private boolean isServerAvailable() {

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(Constants.IP, Constants.PORT), 200);

            return true;

        } catch (IOException e) {

            return false;

        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
}
