package io.github.buraconcio.Screens;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.TCP.Client;
import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Network.TCP.Server;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Objects.UI.Button;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.ConnectionManager;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.GameManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;
import io.github.buraconcio.Utils.Common.CountdownTimer;

public class ServerScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skinTextField, skinLabel;
    private Table topInfo;
    private ImageButton startButton, backButton, mapButton;
    private ImageButtonStyle startStyle, cancelStyle;
    private Label title, mapLabel;
    private boolean started = false, flagBackButton = true;
    private Client cliente = ConnectionManager.getInstance().getClient();
    private Server server = ConnectionManager.getInstance().getServer();
    private boolean firstIn = true;
    private CountdownTimer countdown;

    private Image mapImage;
    public static int mapIndex = 0;
    private final String[] mapPaths = {
            "maps-preview/teste.jpg",
            "maps-preview/teste3.jpg",
    },

    mapNames = {
        "TUTORIAL",
        "FLORESTINHA SEM ARVORE",
    };

    private Texture[] mapTextures;

    public ServerScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        this.skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        Button tempButton = new Button();
        this.startStyle = tempButton.createButtonStyle("start", "start");
        this.cancelStyle = tempButton.createButtonStyle("cancel", "cancel");

        mapTextures = new Texture[mapPaths.length];
        for (int i = 0; i < mapPaths.length; i++) {
            mapTextures[i] = new Texture(Gdx.files.internal(mapPaths[i]));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        CursorManager.resetToArrow();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(createLeftColumn()).expand().fill().left().pad(75);
        root.add(createRightColumn()).expand().fill().top().right().pad(75);

        root.setDebug(false);

        if (!Constants.isHosting() && firstIn) {
            refreshPlayers();
            firstIn = false;
        }

        if (Constants.isHosting()) {

            server = new Server();

            ConnectionManager.getInstance().setServer(server);

            server.startTCPServer();

            PlayerManager.getInstance().clear();

            cliente = new Client();

            ConnectionManager.getInstance().setClient(cliente);

            cliente.startTCPClient();
        }

        cliente.setServerListener(new Client.ServerListener() {

            @Override
            public void PlayerCon() {

                Gdx.app.postRunnable(() -> refreshPlayers());
            }

            @Override
            public void ServerDisconnected() {

                Auxiliaries.clearAddLocal();

                Gdx.app.postRunnable(() -> game.setScreen(new MainMenu(game)));
            }

            @Override
            public void ServerStartMatch() {

                Gdx.app.postRunnable(() -> startMatch());

            }

            @Override
            public void ServerCancelMatch() {

                Gdx.app.postRunnable(() -> cancelMatch());

            }

            @Override
            public void ServerChangeMap(String message) {
                Gdx.app.postRunnable(() -> {

                    try {

                        int receivedIndex = Integer.parseInt(message);
                        mapIndex = receivedIndex;
                        nextMap();

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                });
            }
        });

    }

    private Table createLeftColumn() {
        Table leftColumn = new Table();
        leftColumn.setFillParent(false);

        Table topInfo = new Table();
        topInfo.top().left();

        title = new Label("MATCH LOBBY", skinLabel, "labelPixelyWhite64");
        title.setFontScale(1f);

        Button start = new Button();

        startButton = start.createButton("start", "start");

        Button map = new Button();
        mapButton = map.createButton("map", "map");
        mapLabel = new Label(mapNames[mapIndex], skinLabel, "labelPixelyWhite32");
        topInfo.add(title).left().padBottom(20);
        topInfo.row();
        topInfo.add(startButton).left().padBottom(10).size(280, 112);
        topInfo.row();
        topInfo.add(mapButton).left().size(140, 93);
        topInfo.row();
        topInfo.add(mapLabel).left().padTop(20);
        topInfo.row();

        Table bottomInfo = new Table();
        bottomInfo.bottom().left();
        mapImage = new Image(new Texture(mapPaths[mapIndex]));

        Button back = new Button();
        backButton = back.createButton("back", "back");

        bottomInfo.add(mapImage).left().size(600, 400).padBottom(10);
        bottomInfo.row();
        bottomInfo.add(backButton).left().padBottom(10).size(64, 64);

        startButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                SoundManager.getInstance().playSound("buttonClick");
                if (Constants.isHosting()) {
                    server.changeButton(false);
                }
            }
        });

        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                SoundManager.getInstance().playSound("buttonClick");
                if (!flagBackButton) {
                    return;
                }

                if (Constants.isHosting()) {

                    if (server != null) {
                        server.stop();
                    }

                    if(Constants.DEBUG)
                        System.out.println("host encerrou a sala");

                } else {

                    if (cliente != null) {
                        try {

                            cliente.disconnect();

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }

                    if(Constants.DEBUG)
                        System.out.println("saiu da sala");
                }

                Auxiliaries.clearAddLocal();
                game.setScreen(new MainMenu(game));
            }
        });

        if (!Constants.isHosting()) {
            startButton.clearListeners();
            mapButton.clearListeners();
            startButton.setVisible(false);
            mapButton.setVisible(false);
        }


        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Constants.isHosting()) {
                    SoundManager.getInstance().playSound("buttonClick");
                    mapIndex = (mapIndex + 1) % mapTextures.length;
                    server.sendString(Message.Type.MAP_CHANGE, Integer.toString(mapIndex));
                    nextMap();
                }
            }
        });

        leftColumn.add(topInfo).expandY().top().left();
        leftColumn.row();
        leftColumn.add().expand(); // espaço no meio dos dois / maleavel
        leftColumn.row();
        leftColumn.add(bottomInfo).bottom().left();

        // leftColumn.setDebug(true);
        // topInfo.setDebug(true);
        // bottomInfo.setDebug(true);

        return leftColumn;
    }

    private Table createRightColumn() {
        Table rightColumn = new Table();
        rightColumn.setFillParent(false);
        rightColumn.top().right().pad(40);
        Label playersLabel = new Label("1 Player(s) (4 Max)", skinLabel, "labelPixelyWhite32");

        // playersLabel.setFontScale(0.7f);

        topInfo = new Table();
        topInfo.top().right();

        topInfo.add(playersLabel).left().padBottom(10);
        topInfo.row();

        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
            topInfo.add(createPlayerRow(p.getUsername(), "user-icons/" + p.getAvatar())).left().padBottom(5);
            topInfo.row();
        }

        Table botInfo = new Table();
        Label Alabel = null;

        if (Constants.isHosting()) {
            try {
                Alabel = new Label("IP Server: " + InetAddress.getLocalHost().getHostAddress(), skinLabel,
                        "labelPixelyWhite32");
                Alabel.setFontScale(1f);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        } else {
            Alabel = new Label("Socket Server", skinLabel, "labelPixelyWhite16");
        }

        // Alabel.setFontScale(0.3f);
        Label Blabel = new Label("Criado pela resenha", skinLabel, "labelPixelyWhite16");
        // Blabel.setFontScale(0.3f);

        botInfo.add(Alabel);
        botInfo.row();
        botInfo.add(Blabel);

        rightColumn.add(topInfo).expandY().top().right();
        rightColumn.row();
        rightColumn.add().expand();
        rightColumn.row();
        rightColumn.add(botInfo).bottom().right().padBottom(-25);

        // topInfo.setDebug(true);
        // botInfo.setDebug(true);
        // rightColumn.setDebug(true);

        return rightColumn;
    }

    private Table createPlayerRow(String playerName, String imagePath) {
        Table row = new Table();

        Image image = new Image(new Texture(imagePath));

        TextField playerField = new TextField(playerName, skinTextField, "labelPixelyWhite32");

        playerField.setDisabled(true);

        row.add(image).padRight(8);
        row.add(playerField).width(300);

        return row;
    }

    public void refreshPlayers() {
        topInfo.clear();

        Label playersLabel = new Label(
                PlayerManager.getInstance().getAllPlayers().size() + " Player(s) (4 Max)",
                skinLabel, "labelPixelyWhite32");
        // playersLabel.setFontScale(0.7f);

        topInfo.add(playersLabel).left().padBottom(10);
        topInfo.row();

        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
            topInfo.add(createPlayerRow(p.getUsername(), "user-icons/" + p.getAvatar())).left().padBottom(5);
            topInfo.row();
        }
    }

    public void startMatch() {
        if (!started) {

            startButton.setStyle(cancelStyle);
            mapButton.setVisible(false);

            if (Constants.isHosting()) {
                startButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        cancelMatch();
                        SoundManager.getInstance().playSound("buttonClick");
                        server.changeButton(true);
                    }
                });
            }

            started = true;

            startCountdown();
        }
    }

    private void cancelMatch() {

        startButton.setStyle(startStyle);
        mapButton.setVisible(true);

        flagBackButton = true;

        if (Constants.isHosting()) {
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (Constants.isHosting()) {
                        server.changeButton(false);
                        SoundManager.getInstance().playSound("buttonClick");
                    }
                }
            });
        }

        started = false;

        if (countdown != null) {
            countdown.stop();
        }

        title.setText("MATCH LOBBY");
        title.invalidate();
    }

    private void startCountdown() {
        countdown = new CountdownTimer(3, new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {
                title.setText("MATCH LOBBY - INICIANDO EM " + remainingSecs + "...");
                title.invalidate();
                flagBackButton = false;
            }

            @Override
            public void finish() {

                title.setText("MATCH LOBBY - GO!");
                title.invalidate();

                if (Constants.isHosting()) {
                    server.stopAccepting();
                }
                CountdownTimer delay = new CountdownTimer(1, new CountdownTimer.TimerListener() {
                    @Override
                    public void tick(int remainingSecs) {}

                    @Override
                    public void finish() {

                        //pass the mapIndex
                        GameManager.getInstance().setMapIndex(mapIndex);
                        PhysicsTest screen = new PhysicsTest(game);
                        GameManager.getInstance().setPhysicsScreen(screen);


                        GameManager.getInstance().setCurrentScreen(game, screen);
                    }
                });

                delay.start();
            }

        });

        countdown.start();

    }

    private void nextMap() {
        mapImage.setDrawable(new TextureRegionDrawable(new TextureRegion(mapTextures[mapIndex])));
        mapLabel.setText(mapNames[mapIndex]);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1, true);

        // Desenha o mapa de fundo com opacidade reduzida
        stage.getBatch().begin();
        stage.getBatch().setColor(1, 1, 1, 0.25f); // Opacidade de 25%
        stage.getBatch().draw(mapTextures[mapIndex], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().setColor(1, 1, 1, 1);
        stage.getBatch().end();

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

        for (Texture t : mapTextures) {
            t.dispose();
        }

        if (server != null) {
            server.stop();
        }
    }

}
