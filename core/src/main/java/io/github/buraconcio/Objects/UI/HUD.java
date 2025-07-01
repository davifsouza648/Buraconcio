package io.github.buraconcio.Objects.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Screens.MainMenu;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

public class HUD {
    private Stage stage;
    private Viewport viewport;
    private int playerId;
    private Main game;

    private Button giveUp;
    private Label countdownLabel;
    private Label strokeLabel;
    private Image pauseOverlay;
    private Table mainTable;
    private Table pausedTable;
    private Button resume, quit, config;
    private Table volumeMainTable;
    private Slider sliderVolumeSounds, sliderVolumeMusic;
    private Label labelMusicVolume, labelSoundVolume;
    private Button backConfig;
    private Timer.Task countdownTask;
    private int remainingSeconds;
    private boolean isButtonPressed;
    private boolean isPaused = false;
    private boolean isConfig = false;

    private Label clockLabel;

    public HUD(Stage stage, int playerId, Main game) {
        this.playerId = playerId;
        this.game = game;
        this.stage = stage;
        this.viewport = stage.getViewport();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        pauseOverlay = new Image(texture);
        pauseOverlay.setFillParent(true);
        pauseOverlay.setVisible(false);
        stage.addActor(pauseOverlay);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(false);

        Skin skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        Skin skinLabelStroke = new Skin(Gdx.files.internal("fonts/hachiro/label/labelHachiro.json"));
        Skin skinSlider = new Skin(Gdx.files.internal("slider/slider.json"));

        countdownLabel = new Label("", skinLabel, "labelPixelyWhite32");
        countdownLabel.setVisible(false);
        countdownLabel.setScale(3f);
        countdownLabel.setColor(1, 0, 0, 1);

        clockLabel = new Label("00:00", skinLabelStroke, "hachiro");
        clockLabel.setScale(2f);
        clockLabel.setVisible(true);

        strokeLabel = new Label("Strokes: " + PlayerManager.getInstance().getLocalPlayer().getStrokes(),
                skinLabelStroke, "hachiro");
        strokeLabel.setVisible(true);
        strokeLabel.setScale(2f);

        Table topLeftTable = new Table();
        topLeftTable.top().left().pad(10);
        topLeftTable.setFillParent(true);
        topLeftTable.add(strokeLabel).pad(10);
        stage.addActor(topLeftTable);

        Table topRightTable = new Table();
        topRightTable.top().right().pad(10);
        topRightTable.setFillParent(true);
        topRightTable.add(clockLabel).pad(10);
        stage.addActor(topRightTable);

        mainTable.add(countdownLabel).center().expand();

        Table buttonTable = new Table();
        buttonTable.bottom().left();

        giveUp = new Button();
        ImageButton giveUpButton = giveUp.createButton("giveUp", "giveup");

        giveUpButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startCountdown();
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                cancelCountdown();
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (x < 0 || x > giveUpButton.getWidth() || y < 0 || y > giveUpButton.getHeight()) {
                    cancelCountdown();
                }
            }
        });

        buttonTable.add(giveUpButton).pad(10).size(100, 50);
        mainTable.row();
        mainTable.add(buttonTable).bottom().left().padBottom(20).padLeft(20).expandX();
        stage.addActor(mainTable);

        pausedTable = new Table();
        pausedTable.setFillParent(true);
        pausedTable.setVisible(false);

        resume = new Button();
        quit = new Button();
        config = new Button();

        ImageButton resumeButton = resume.createButton("resume", "resume");
        ImageButton quitButton = quit.createButton("quit", "quit");
        ImageButton configButton = config.createButton("config", "config");

        resumeButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isPaused())
                    togglePaused();
                SoundManager.getInstance().playSound("buttonClick");
                return true;
            }
        });

        quitButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                if (isPaused) {
                    if (Constants.isHosting()) {
                        ConnectionManager.getInstance().getServer().stop();
                    } else {
                        ConnectionManager.getInstance().getClient().quitFunction();
                    }

                    GameManager.getInstance().setCurrentScreen(game, new MainMenu(game));
                    SoundManager.getInstance().stopMusic();
                    SoundManager.getInstance().playMusic("menu");
                    SoundManager.getInstance().playSound("buttonClick");
                    dispose();
                }
                return true;
            }
        });

        configButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                toggleConfig();
                return true;
            }
        });

        Table pausedButtonTable = new Table();
        pausedButtonTable.center();
        pausedButtonTable.add(resumeButton).size(140, 80).pad(10);
        pausedButtonTable.row();
        pausedButtonTable.add(quitButton).size(140, 80).pad(10);

        Table configButtonTable = new Table();
        configButtonTable.bottom().left();
        configButtonTable.add(configButton).size(50, 50).pad(10);

        pausedTable.add(pausedButtonTable).expand().center();
        pausedTable.row();
        pausedTable.add(configButtonTable).expandX().fillX().bottom().left();
        stage.addActor(pausedTable);

        volumeMainTable = new Table();
        volumeMainTable.setFillParent(true);
        volumeMainTable.center();
        volumeMainTable.setVisible(false);

        sliderVolumeMusic = new Slider(0f, 1f, 0.1f, false, skinSlider, "default-horizontal");
        sliderVolumeSounds = new Slider(0f, 1f, 0.1f, false, skinSlider, "default-horizontal");

        sliderVolumeMusic.setValue(SoundManager.getInstance().getMasterVolumeMusic());
        sliderVolumeSounds.setValue(SoundManager.getInstance().getMasterVolumeSounds());

        labelMusicVolume = new Label("Music volume: ", skinLabel, "labelPixelyWhite32");
        labelSoundVolume = new Label("Sound volume: ", skinLabel, "labelPixelyWhite32");

        backConfig = new Button();
        ImageButton backConfigButton = backConfig.createButton("back", "backc");

        sliderVolumeMusic.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setMasterVolumeMusic(sliderVolumeMusic.getValue());
            }
        });
        sliderVolumeSounds.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setMasterVolumeSounds(sliderVolumeSounds.getValue());
            }
        });

        backConfigButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                toggleConfig();
                return true;
            }
        });

        volumeMainTable.add(labelMusicVolume).size(300, 50).padTop(15).padBottom(5).center();
        volumeMainTable.row();
        volumeMainTable.add(sliderVolumeMusic).width(300).height(40).padBottom(15).center();
        volumeMainTable.row();
        volumeMainTable.add(labelSoundVolume).size(300, 50).padTop(15).padBottom(5).center();
        volumeMainTable.row();
        volumeMainTable.add(sliderVolumeSounds).width(300).height(40).padBottom(25).center();
        volumeMainTable.row();
        volumeMainTable.add(backConfigButton).size(140, 60).pad(10).center();
        stage.addActor(volumeMainTable);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void updateStrokes() {
        strokeLabel.setText("Strokes: " + PlayerManager.getInstance().getLocalPlayer().getStrokes());
    }

    public void updateClock(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        clockLabel.setText(String.format("%02d:%02d", minutes, secs));
    }

    public void togglePaused() {
        isPaused = !isPaused;
        if (!isPaused) {
            isConfig = false;
            volumeMainTable.setVisible(false);
        }
        pauseOverlay.setVisible(isPaused);
        pausedTable.setVisible(isPaused && !isConfig);
        mainTable.setVisible(!isPaused);
    }

    public void toggleConfig() {
        isConfig = !isConfig;
        if (isConfig) {
            pausedTable.setVisible(false);
            volumeMainTable.setVisible(true);
        } else {
            pausedTable.setVisible(true);
            volumeMainTable.setVisible(false);
        }
        pauseOverlay.setVisible(isPaused);
        mainTable.setVisible(!isPaused);
    }

    private void startCountdown() {
        isButtonPressed = true;
        remainingSeconds = 3;
        countdownLabel.setVisible(true);
        updateCountdownText();
        if (countdownTask != null)
            countdownTask.cancel();
        countdownTask = new Timer.Task() {
            public void run() {
                remainingSeconds--;
                updateCountdownText();
                if (remainingSeconds <= 0) {
                    PlayerManager.getInstance().getPlayer(playerId).die();
                    cancelCountdown();
                }
            }
        };
        Timer.schedule(countdownTask, 1, 1, 2);
    }

    private void cancelCountdown() {
        isButtonPressed = false;
        if (countdownTask != null)
            countdownTask.cancel();
        countdownTask = null;
        countdownLabel.setVisible(false);
    }

    private void updateCountdownText() {
        countdownLabel.setText(String.valueOf(remainingSeconds));
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        updateStrokes();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (countdownTask != null)
            countdownTask.cancel();
        stage.dispose();
    }
}
