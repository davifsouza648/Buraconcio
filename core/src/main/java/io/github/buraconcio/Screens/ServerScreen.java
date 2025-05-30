package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Network.Server;
import io.github.buraconcio.Objects.Player;
import io.github.buraconcio.Objects.Button;
import io.github.buraconcio.Utils.PlayerManager;

public class ServerScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private Table topInfo;
    private Server server;
    private Client cliente;

    public ServerScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    @Override
    public void show() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(createLeftColumn()).expand().fill().left().pad(75);
        root.add(createRightColumn()).expand().fill().top().right().pad(75);

        root.setDebug(false);

        if (PlayerManager.getInstance().getLocalPlayer().getHosting().equals(true)) {

            this.server = new Server();
            server.startTCPServer();

            PlayerManager.getInstance().clear();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.cliente = new Client();

        cliente.setPlayerConnectedListener(() -> {
            Gdx.app.postRunnable(() -> {
                refreshPlayers();
            });
        });

        cliente.startTCPClient();

    }

    private Table createLeftColumn() {
        Table leftColumn = new Table();
        leftColumn.setFillParent(false);

        Table topInfo = new Table();
        topInfo.top().left();
        Label title = new Label("MATCH LOBBY", skin);
        title.setFontScale(2.5f);

        Button start = new Button();
        ImageButton startButton = start.createButton("start", "start");
        Button map = new Button();
        ImageButton mapButton = map.createButton("map", "map");

        topInfo.add(title).left().padBottom(20);
        topInfo.row();
        topInfo.add(startButton).left().padBottom(10).size(160, 64);
        topInfo.row();
        topInfo.add(mapButton).left().padBottom(20).size(96, 64);

        Table bottomInfo = new Table();
        bottomInfo.bottom().left();
        Image mapImage = new Image(new Texture("teste.jpg"));

        Button back = new Button();
        ImageButton backButton = back.createButton("back", "back");

        bottomInfo.add(mapImage).left().size(600, 400).padBottom(10);
        bottomInfo.row();
        bottomInfo.add(backButton).left().padBottom(10).size(64, 64);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                System.out.println("Start Match pressionado!");

                if (PlayerManager.getInstance().getLocalPlayer().getHosting()) {

                    if (server != null) {
                        //TODO: timer + troca de botao;
                        server.stopAccepting(); //TODO: verificar se stopAccepting esta funfando
                    }

                }
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // if(PlayerManager.getInstance().getLocalPlayer().getHosting()){
                //
                // fazerr todos voltarem ao menu e fechar o server;
                //
                // }else{
                //
                // sair do lobby e atualizar (clear no PLayerManager e mensagem para o server para sairr do deles)
                //
                // }

                for(Player a : PlayerManager.getInstance().getAllPlayers()){
                    System.out.println(a.getUsername());
                }

                game.setScreen(new MainMenu(game));
            }
        });

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
        Label playersLabel = new Label("4 Player(s) (4 Max)", skin); // receber N de algum lugar
        playersLabel.setFontScale(1.1f);

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

        Label Alabel = new Label("Socket Server", skin);
        Label Blabel = new Label("Criado pela resenha", skin);

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

        TextField playerField = new TextField(playerName, skin);

        playerField.setDisabled(true);

        row.add(image).padRight(8);
        row.add(playerField).width(300);

        return row;
    }

    // LEMBRAR DE PUXAR NA BOMBA DO SERVER TODA VEZ QUE ENTRAR ALGUEM NO SERVER;

    public void refreshPlayers() {
        topInfo.clear();

        Label playersLabel = new Label(
                PlayerManager.getInstance().getAllPlayers().size() + " Player(s) (4 Max)",
                skin);
        playersLabel.setFontScale(1.1f);

        topInfo.add(playersLabel).left().padBottom(10);
        topInfo.row();

        for (Player p : PlayerManager.getInstance().getAllPlayers()) {
            topInfo.add(createPlayerRow(p.getUsername(), "user-icons/" + p.getAvatar())).left().padBottom(5);
            topInfo.row();
        }
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
