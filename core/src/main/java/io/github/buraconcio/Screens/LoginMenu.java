package io.github.buraconcio.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Objects.UI.Button;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Managers.CursorManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;
import io.github.buraconcio.Utils.Managers.SoundManager;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.*;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.*;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;


public class LoginMenu implements Screen {

    private Main game;
    private Stage stage;
    private TextField userField, passField;
    private Label warningUserLabel;
    private Skin skinTextField, skinLabel;
    private String username;
    // private String password;

    private GDXDialogs dialogs;
    private String[] skinBallpaths ={
        "balls/blackBall.png",
        "balls/blueBall.png",
        "balls/cianBall.png",
        "balls/grayBall.png",
        "balls/greenBall.png",
        "balls/orangeBall.png",
        "balls/pinkBall.png",
        "balls/purpleBall.png",
    };

    private SpriteBatch batch;
    private Texture texture;
    private TextureRegion backgroundRegion;

    private float scrollX = 0;
    private float scrollY = 0;
    private float scrollSpeed = 50f;
    private float textureScale = 2f;

    private Image titleImage;




    public LoginMenu(Main game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        SoundManager.getInstance().loadSound("buttonClick", "sounds/UI/buttonClick.wav");

        //Se clicar fora do textField ele perde o foco
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor target = event.getTarget();

                if (!(target instanceof com.badlogic.gdx.scenes.scene2d.ui.TextField)) {
                    stage.setKeyboardFocus(null);
                }
                return false;
            }
        });

        batch = new SpriteBatch();

        texture = new Texture(Gdx.files.internal("backgrounds/loginScreen/greenprint.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        backgroundRegion = new TextureRegion(texture);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        backgroundRegion.setRegion(0, 0, width, height);

        dialogs = GDXDialogsSystem.install();

        skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().center();
        stage.addActor(table);
        // table.setDebug(true);
        table.setDebug(false); // tiltei

        userField = new TextField("", skinTextField, "labelPixelyWhite32");
        userField.setMessageText("Digite seu username");

        //Não permite que o usuário possa digitar mais que 10 caracteres no nome
        userField.setTextFieldFilter((textField, c) -> {
            return textField.getText().length() < 10;
        });
        //Seta Ibeam Cursor
        CursorManager.applyIbeamCursorOnHover(userField);

        Texture titleTextute = new Texture(Gdx.files.internal("backgrounds/loginScreen/login.png"));
        titleImage = new Image(titleTextute);

        Button entrar = new Button();
        ImageButton loginButton = entrar.createButton("enter", "enter");

        Button sair = new Button();
        ImageButton quitButton = sair.createButton("quit", "quit");

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                username = userField.getText();
                SoundManager.getInstance().playSound("buttonClick");
                if(isUsernameValid(username))
                {
                    Player player = new Player(username);

                    Random random = new Random();

                    player.setId(username.hashCode()); // + random.nextInt(25));

                    player.setAvatar(random.nextInt(1, 32));
                    int skinBall = random.nextInt(0, 7);
                    player.setSkinBall(skinBallpaths[skinBall]);

                    PlayerManager.getInstance().setLocalPlayer(player);

                    if(Constants.DEBUG)
                        System.out.println("Usuario: " + player.getUsername());

                    game.setScreen(new MainMenu(game));
                }
                userField.setText("");
            }
        });
        table.add(titleImage).padBottom(20).size(500, 113).padBottom(50);
        table.row();
        table.add(userField).width(500).pad(50);
        table.row();
        // table.add(passField).width(200).pad(10);
        // table.row();
        table.add(loginButton).size(200, 80).colspan(2).padTop(10);
        table.row();
        table.add(quitButton).size(200, 80).colspan(2).padTop(10);
    }

    private boolean isUsernameValid(String username)
    {
        boolean valid = true;
        if(username.length() == 0)
        {
            showErrorMessage("Nome inválido", "Motivo: Nome nulo");
            valid = false;
        }

        return valid;
    }

    private void showErrorMessage(String title, String text)
    {
        GDXButtonDialog bDialog = dialogs.newDialog(GDXButtonDialog.class);
        bDialog.setTitle(title);
        bDialog.setMessage(text);

        bDialog.setClickListener(new ButtonClickListener()
        {
            @Override
            public void click(int button) {
                bDialog.dismiss();
            }
        });

        bDialog.addButton("Ok");

        bDialog.build().show();
    }

    @Override
    public void show() {
        CursorManager.resetToArrow();

    }

    @Override
    public void render(float delta) {

        scrollX += scrollSpeed * delta;
        scrollY += scrollSpeed * delta;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        backgroundRegion.setRegion(
            (int)scrollX, (int)scrollY,
            (int)(width / textureScale),
            (int)(height / textureScale)
        );

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundRegion, 0, 0, width, height);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        backgroundRegion.setRegion((int)scrollX, (int)scrollY, width, height);
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
        batch.dispose();
        texture.dispose();
        stage.dispose();
        skinTextField.dispose();
    }

}
