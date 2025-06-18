package io.github.buraconcio.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;

import io.github.buraconcio.Utils.PlayerManager;

public class HUD {
    private Stage stage;
    private Viewport viewport;
    private int playerId;

    private Button giveUp;
    private Label countdownLabel;
    private Timer.Task countdownTask;
    private int remainingSeconds;
    private boolean isButtonPressed;

    public HUD(Stage stage, int playerId) 
    {
        this.playerId = playerId;

        OrthographicCamera hudCamera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, hudCamera);
        this.stage = stage;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(false); 

        Skin skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        countdownLabel = new Label("", skinLabel, "labelPixelyWhite32");
        countdownLabel.setVisible(false); 
        countdownLabel.setScale(3f);
        
        mainTable.add(countdownLabel).center().expand();

        Table buttonTable = new Table();
        buttonTable.bottom().left();
        
        giveUp = new Button();
        ImageButton giveUpButton = giveUp.createButton("giveUp", "giveup");

        giveUpButton.addListener(new InputListener() 
        {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startCountdown();
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
            {
                cancelCountdown();
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) 
            {
                if (x < 0 || x > giveUpButton.getWidth() || y < 0 || y > giveUpButton.getHeight()) 
                {
                    cancelCountdown();
                }
            }
        });
        
        buttonTable.add(giveUpButton).pad(10).size(100, 50);

        mainTable.row(); 
        mainTable.add(buttonTable).bottom().left().padBottom(20).padLeft(20);

        stage.addActor(mainTable);
    }

    private void startCountdown() 
    {
        isButtonPressed = true;
        remainingSeconds = 3;
        countdownLabel.setVisible(true);
        updateCountdownText();

        if (countdownTask != null) 
        {
            countdownTask.cancel();
        }

        countdownTask = new Timer.Task() 
        {
            @Override
            public void run() 
            {
                remainingSeconds--;
                updateCountdownText();

                if (remainingSeconds <= 0) 
                {
                    PlayerManager.getInstance().getPlayer(playerId).die();
                    cancelCountdown();
                    countdownLabel.setColor(1,1,1,1);
                }
            }
        };

        Timer.schedule(countdownTask, 1, 1, 2);
    }

    private void cancelCountdown() 
    {
        isButtonPressed = false;
        if (countdownTask != null) 
        {
            countdownTask.cancel();
            countdownTask = null;
        }
        countdownLabel.setVisible(false);
        countdownLabel.setColor(1,1,1,1);
    }

    private void updateCountdownText() 
    {
        countdownLabel.setText(String.valueOf(remainingSeconds));
        if(remainingSeconds < 2)
        {
            countdownLabel.setColor(1,0,0,1);
        }
        // countdownLabel.setColor(1, 0, 0, 1); // Texto vermelho
        // countdownLabel.setFontScale(1.5f); // Aumenta o tamanho
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        stage.dispose();
    }
}
