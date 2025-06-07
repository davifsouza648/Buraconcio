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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.Server;
import io.github.buraconcio.Network.UDPClient;
import io.github.buraconcio.Network.UDPServer;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Button;
import io.github.buraconcio.Utils.Auxiliaries;
import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.CursorManager;
import io.github.buraconcio.Utils.PlayerManager;

public class ServerScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skinTextField, skinLabel;
    private Table topInfo;
    private ImageButton startButton, backButton;
    private ImageButtonStyle startStyle, cancelStyle;
    private Label title;
    private boolean started = false, flagBackButton = true;
    private boolean isHosting = PlayerManager.getInstance().getLocalPlayer().getHosting();
    private Client cliente = ConnectionManager.getInstance().getClient();
    private Server server = ConnectionManager.getInstance().getServer();
    private UDPServer udpServer = ConnectionManager.getInstance().getUdpServer();
    private Timer.Task countdownTask;
    private boolean firstIn = true;

    public ServerScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        this.skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        Button tempButton = new Button();
        this.startStyle = tempButton.createButtonStyle("start", "start");
        this.cancelStyle = tempButton.createButtonStyle("cancel", "cancel");
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

        if (!isHosting && firstIn) {
            refreshPlayers();
            firstIn = false;
        }

        if (isHosting) {

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
        ImageButton mapButton = map.createButton("map", "map");

        topInfo.add(title).left().padBottom(20);
        topInfo.row();
        topInfo.add(startButton).left().padBottom(10).size(280, 112);
        topInfo.row();
        topInfo.add(mapButton).left().padBottom(20).size(140, 93);

        Table bottomInfo = new Table();
        bottomInfo.bottom().left();
        Image mapImage = new Image(new Texture("teste.jpg"));

        Button back = new Button();
        backButton = back.createButton("back", "back");

        bottomInfo.add(mapImage).left().size(600, 400).padBottom(10);
        bottomInfo.row();
        bottomInfo.add(backButton).left().padBottom(10).size(64, 64);

        startButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                System.out.println("Start Match pressionado!");

                if (isHosting) {
                    server.changeButton(false);
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (!flagBackButton) {
                    return;
                }

                if (isHosting) {

                    if (server != null) {
                        server.stop();
                    }

                    System.out.println("host encerrou a sala");

                } else {

                    if (cliente != null) {
                        try {

                            cliente.disconnect();

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }

                    System.out.println("saiu da sala");
                }

                // PlayerManager.getInstance().clear();
                // PlayerManager.getInstance().addPlayer(PlayerManager.getInstance().getLocalPlayer());

                Auxiliaries.clearAddLocal();
                game.setScreen(new MainMenu(game));
            }
        });

        if (!isHosting) {
            startButton.clearListeners();
        }

        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

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

        // topInfo.add(createPlayerRow("Arthur",
        // "user-icons/user1.png")).left().padBottom(5);
        // topInfo.row();
        // topInfo.add(createPlayerRow("Davi",
        // "user-icons/user2.png")).left().padBottom(5);
        // topInfo.row();
        // topInfo.add(createPlayerRow("Mario",
        // "user-icons/user3.png")).left().padBottom(5);
        // topInfo.row();
        // topInfo.add(createPlayerRow("Murilo",
        // "user-icons/user4.png")).left().padBottom(5);

        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
            topInfo.add(createPlayerRow(p.getUsername(), "user-icons/" + p.getAvatar())).left().padBottom(5);
            topInfo.row();
        }

        Table botInfo = new Table();
        Label Alabel = null;

        if (isHosting) {
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

            if (isHosting) {
                startButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        cancelMatch();
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
        flagBackButton = true;

        if (isHosting) {
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isHosting) {
                        server.changeButton(false);
                    }
                }
            });
        }

        started = false;

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        title.setText("MATCH LOBBY");
        title.invalidate();
    }

    private void startCountdown() {
        final int[] count = { 10 };

        countdownTask = new Timer.Task() {
            @Override
            public void run() {
                if (count[0] > 0) {
                    title.setText("MATCH LOBBY - INICIANDO EM " + count[0] + "...");
                    title.invalidate();
                    count[0]--;

                    flagBackButton = false;

                } else {

                    if (isHosting) {
                        server.stopAccepting();

                        UDPServer udpServer = new UDPServer();
                        ConnectionManager.getInstance().setUDPserver(udpServer);
                        udpServer.startUDPServer();

                    }

                    title.setText("MATCH LOBBY - GO!");
                    title.invalidate();
                    this.cancel();

                    if (isHosting) {
                        game.setScreen(new PhysicsTest(game)); // TODO: LEMBRAR DA TELA DE GAME AQUI
                    } else {
                        game.setScreen(new PhysicsTest(game));
                    }
                }
            }
        };

        Timer.schedule(countdownTask, 0, 1);
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

        if (server != null) {
            server.stop();
        }
    }

}
