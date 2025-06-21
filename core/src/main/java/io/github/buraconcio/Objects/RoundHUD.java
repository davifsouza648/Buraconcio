package io.github.buraconcio.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RoundHUD {

    private Stage stage;

    private Image background;
    private Image topBar;
    private Image rightPanel;
    private Image leftLine;
    private Image[] playerCircles = new Image[4];

    public RoundHUD() {
        stage = new Stage(new ScreenViewport());

        // 🔹 Fundo blur
        background = new Image(new Texture("rounds/back/background.png"));

        float bgWidth = Gdx.graphics.getWidth() * 0.85f;
        float bgHeight = Gdx.graphics.getHeight() * 0.85f;

        background.setSize(bgWidth, bgHeight);
        float bgX = (Gdx.graphics.getWidth() - bgWidth) / 2;
        float bgY = (Gdx.graphics.getHeight() - bgHeight) / 2;
        background.setPosition(bgX, bgY);

        stage.addActor(background);

        topBar = new Image(new Texture("rounds/top/round.png"));
        float topBarX = bgX + (bgWidth - topBar.getWidth()) / 2;
        float topBarY = bgY + bgHeight - topBar.getHeight() - 20;
        topBar.setPosition(topBarX, topBarY);
        stage.addActor(topBar);

        // rightPanel = new Image(new Texture("hole.png"));
        // float rightPanelX = bgX + bgWidth - rightPanel.getWidth() - 40;
        // float rightPanelY = bgY + (bgHeight - rightPanel.getHeight()) / 2;
        // rightPanel.setPosition(rightPanelX, rightPanelY);
        // stage.addActor(rightPanel);

        // leftLine = new Image(new Texture("crossbow.png"));
        // float leftLineX = bgX + 120;
        // float leftLineY = bgY + 40;
        // leftLine.setSize(5, bgHeight - 80); // Linha vertical
        // leftLine.setPosition(leftLineX, leftLineY);
        // stage.addActor(leftLine);

        for (int i = 0; i < 4; i++) {
            playerCircles[i] = new Image(new Texture("ball.png"));
            float circleX = bgX + 60;
            float firstCircleY = bgY + bgHeight - 150;
            float circleY = firstCircleY - i * 120;
            playerCircles[i].setPosition(circleX, circleY);
            playerCircles[i].setSize(100, 100);;
            stage.addActor(playerCircles[i]);
        }
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
