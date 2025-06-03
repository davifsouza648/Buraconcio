package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.*;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Client;
import io.github.buraconcio.Objects.Button;
import io.github.buraconcio.Utils.ConnectionManager;
import io.github.buraconcio.Utils.Constants;

public class LoadingScreen implements Screen {
    private final Main game;
    private Client cliente;
    private boolean flagCon = false, flagFail = false, flagMsg = false, showImage = false;
    private final Stage stage;

    private final float TIMEOUT = 4f;
    private float elapsedTime = 0f;

    private Texture texture;
    private float x, y;
    private float speedX = 220;
    private float speedY = 220;
    private SpriteBatch batch;
    private Skin skinLabel, skinTextField;
    private Label title;
    private float messageTimer = 0f;
    private final float MESSAGE_DURATION = 4f;

    private GDXDialogs dialogs;
    
    public LoadingScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        dialogs = GDXDialogsSystem.install();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        setupTitle();
        setupLogo();
        createTable();
    }

    private void setupTitle() {
        title = new Label("", skinLabel, "labelPixelyWhite64");
        title.setFontScale(1f);
        title.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);
        title.setAlignment(Align.center);
        title.setVisible(false);
        stage.addActor(title);
    }

    private void setupLogo() {
        texture = new Texture(Gdx.files.internal("Logo64x64.png"));
        x = 0;
        y = stage.getHeight() / 2f - texture.getHeight() / 2f;
    }

    private void createTable() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label titleP1 = new Label("Server Selection", skinLabel, "labelPixelyWhite64");
        title.setFontScale(0.8f);

        TextField ipField = new TextField("", skinTextField, "labelPixelyWhite32");
        ipField.setMessageText("Digite o IP");

        Button entrar = new Button();
        ImageButton confirmButton = entrar.createButton("enter", "enter");

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String _ipCorrect = "";

                if (isIpvalid(ipField, _ipCorrect)) {
                    handleConnection(table, _ipCorrect);
                }else{
                    showErrorMessage("IP invalid", "Digite o IP (ipv4) corretamente (exemplo: xxx.xxx.xx.x)");
                    ipField.setText("");
                }
            }
        });

        table.add(titleP1).padBottom(10);
        table.row();
        table.add(ipField).width(500).pad(10);
        table.row();
        table.add(confirmButton).size(200, 80).colspan(2).padTop(10);
    }

    private boolean isIpvalid(TextField ipField, String _ipCorrect) {
        String _ip = ipField.getText().trim();
        boolean flagInit = false;

        if (_ip.equalsIgnoreCase("localhost")) {
            _ipCorrect = _ip;
            flagInit = true;
        } else if (isValidIPv4(_ip)) {
            _ipCorrect = _ip;
            flagInit = true;
        } else {
            System.out.println("Ip Invalido");
            flagInit = false;
        }

        return flagInit;
    }

    private void handleConnection(Table table, String ipText) {
        table.setVisible(false);
        showImage = true;

        Constants.setIP("localhost"); // davizao fazer uma verificacao aqui para colocar os pontos no ip

        cliente = new Client();
        ConnectionManager.getInstance().setClient(cliente);

        cliente.setServerListener(new Client.ServerListener() {
            @Override
            public void PlayerCon() {
                flagCon = true;
                flagMsg = true;
                messageTimer = 0;
                Gdx.app.postRunnable(() -> showMessage("Servidor encontrado!"));
            }

            @Override
            public void ServerDisconnected() {
                flagFail = true;
                flagMsg = true;
                messageTimer = 0;
                Gdx.app.postRunnable(() -> showMessage("Nenhum servidor encontrado!"));
            }

            @Override
            public void ServerStartMatch() {
            }

            @Override
            public void ServerCancelMatch() {
            }
        });

        cliente.startTCPClient();
    }

    private void showErrorMessage(String title, String text) {
        GDXButtonDialog bDialog = dialogs.newDialog(GDXButtonDialog.class);
        bDialog.setTitle(title);
        bDialog.setMessage(text);

        bDialog.setClickListener(new ButtonClickListener() {
            @Override
            public void click(int button) {
                bDialog.dismiss();
            }
        });

        bDialog.addButton("Ok");

        bDialog.build().show();
    }

    private void showMessage(String msg) {
        title.setText(msg);
        title.setVisible(true);
    }

    private void updatePos(float delta) {
        x += speedX * delta;
        y += speedY * delta;

        if (x <= 0) {
            x = 0;
            speedX = -speedX;
        } else if (x + texture.getWidth() >= stage.getWidth()) {
            x = stage.getWidth() - texture.getWidth();
            speedX = -speedX;
        }

        if (y <= 0) {
            y = 0;
            speedY = -speedY;
        } else if (y + texture.getHeight() >= stage.getHeight()) {
            y = stage.getHeight() - texture.getHeight();
            speedY = -speedY;
        }
    }

    private void updateMsg(float delta) {
        if (flagMsg) {
            messageTimer += delta;

            if (messageTimer >= MESSAGE_DURATION) {
                title.setVisible(false);
                flagMsg = false;

                if (flagCon) {
                    game.setScreen(new ServerScreen(game));
                } else if (flagFail) {
                    game.setScreen(new MainMenu(game));
                }
                
            }
        } else {
            if (!flagCon && flagFail && elapsedTime >= TIMEOUT) {
                flagFail = true;
                flagMsg = true;
                messageTimer = 0;
            }
        }
    }

    private static boolean isValidIPv4(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|1?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1?[0-9]{1,2})$";
        return ip.matches(ipv4Pattern);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        updateMsg(delta);
        updatePos(delta);

        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();

        if (showImage) {
            batch.begin();
            batch.draw(texture, x, y);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        title.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);
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
        texture.dispose();
        batch.dispose();
    }
}
