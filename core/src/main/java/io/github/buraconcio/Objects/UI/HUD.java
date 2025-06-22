package io.github.buraconcio.Objects.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Screens.MainMenu;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class HUD
{
    private Stage stage;
    private Viewport viewport;
    private int playerId;
    private Main game;

    //Coisas do hud normal
    private Button giveUp;
    private Label countdownLabel;
    private Image pauseOverlay;
    private Table mainTable;

    //Coisas do hud pausado
    private Table pausedTable;
    private Button resume;
    private Button quit;
    private Button config;

    private Timer.Task countdownTask;
    private int remainingSeconds;
    private boolean isButtonPressed;


    private boolean isPaused = false;

    public HUD(Stage stage, int playerId, Main game)
    {
        this.playerId = playerId;
        this.game = game;

        OrthographicCamera hudCamera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, hudCamera);
        this.stage = stage;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        pauseOverlay = new Image(texture);
        pauseOverlay.setFillParent(true);
        pauseOverlay.setVisible(false);

        stage.addActor(pauseOverlay);

        //Hud fora do pause
        mainTable = new Table();
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
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
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

        //Hud pausada

        pausedTable = new Table();
        pausedTable.setFillParent(true);
        pausedTable.setVisible(false);

        resume = new Button();
        quit = new Button();
        config = new Button();

        ImageButton resumeButton = resume.createButton("resume", "resume");
        ImageButton quitButton = quit.createButton("quit", "quit");
        ImageButton configButton = config.createButton("config", "config");

        resumeButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
            {
                if(isPaused()) togglePaused();
                SoundManager.getInstance().playSound("buttonClick");
                return true;
            }
        });

        quitButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button)
            {
                if(isPaused)
                {
                    GameManager.getInstance().setCurrentScreen(game, new MainMenu(game));
                    SoundManager.getInstance().playMusic("menu");
                    SoundManager.getInstance().playSound("buttonClick");
                    ConnectionManager.getInstance().getServer().sendString(Message.Type.DISCONNECT, "get out"); //DEPOIS MURILO ARRUMA.
                    dispose();
                }
                return true;
            }
        });

        configButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button)
            {
                return true;
            }
        });

        Table pausedButtonTable = new Table();
        pausedButtonTable.center();
        pausedButtonTable.add(resumeButton).size(100,50).pad(10);
        pausedButtonTable.row();
        pausedButtonTable.add(quitButton).size(100,50).pad(10);

        Table configButtonTable = new Table();
        configButtonTable.bottom().left();
        configButtonTable.add(configButton).size(50,50).pad(10);

        pausedTable.add(pausedButtonTable).expand().center();
        pausedTable.row();
        pausedTable.add(configButtonTable).expandX().fillX().bottom().left();

        stage.addActor(pausedTable);

    }

    public boolean isPaused()
    {
        return isPaused;
    }

    public void togglePaused()
    {
        isPaused = !isPaused;
        pauseOverlay.setVisible(isPaused);
        pausedTable.setVisible(isPaused);
        mainTable.setVisible(!isPaused);
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
    }

    public void render()
    {
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
