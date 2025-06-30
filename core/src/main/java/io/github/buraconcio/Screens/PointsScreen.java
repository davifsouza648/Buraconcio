package io.github.buraconcio.Screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.damios.guacamole.gdx.assets.Text;
import io.github.buraconcio.Main;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Auxiliaries;
import io.github.buraconcio.Utils.Managers.FlowManager;
import io.github.buraconcio.Utils.Managers.PlayerManager;

public class PointsScreen implements Screen {

    private final Main game;
    private final Stage stage;
    private final Skin skinTextField, skinLabel;
    private Texture textureBG, textureRound;
    private Image imageBG, imageRound;

    private float elapsedTime = 0f;
    private final List<AnimatedPoint> animatedPoints = new ArrayList<>();

    private String[] pointsPaths = 
    {
        "points/blue",
        "points/lightCian",
        "points/lightGray",
        "points/lightGreen",
        "points/lightOrange",
        "points/lightPurple",
        "points/pink",
        "points/white"
    };

    private String[] numberPaths = 
    {
        "rounds/top/0.png",
        "rounds/top/1.png",
        "rounds/top/2.png",
        "rounds/top/3.png",
        "rounds/top/4.png",
        "rounds/top/5.png",
        "rounds/top/6.png",
        "rounds/top/7.png",
        "rounds/top/8.png",
        "rounds/top/9.png"
    };

    ArrayList<Texture> numTextures = new ArrayList<>();
    ArrayList<Texture> skinTextures = new ArrayList<>();
    Random r;

    public PointsScreen(Main game) {

        this.game = game;

        this.stage = new Stage(new ScreenViewport());
        this.skinTextField = new Skin(Gdx.files.internal("fonts/pixely/textFields/textField.json"));
        this.skinLabel = new Skin(Gdx.files.internal("fonts/pixely/labels/labelPixely.json"));

        this.textureBG = new Texture(Gdx.files.internal("rounds/back/background.png"));
        this.imageBG = new Image(this.textureBG);
        imageBG.setFillParent(true);

        this.textureRound = new Texture(Gdx.files.internal("rounds/top/round.png"));
        this.imageRound = new Image(textureRound);

        r = new Random();
    }

    @Override
    public void show() 
    {
        animatedPoints.clear();
        Table root = new Table();
        root.setFillParent(true);
        root.top();

        Table mainTable = new Table();
        mainTable.center();

        Table topTable = new Table();
        topTable.top();

        String roundNumber = String.valueOf(FlowManager.getInstance().getRound());

        topTable.add(imageRound).center().pad(5).row();

        for (int i = 0; i < roundNumber.length(); i++) 
        {
            char c = roundNumber.charAt(i);
            Texture tex = new Texture(numberPaths[c - '0']);
            numTextures.add(tex);
            topTable.add(new Image(tex)).pad(2); 
        }

        for (Player p : PlayerManager.getInstance().getAllPlayers()) 
        {
            Table playerTable = new Table();
            playerTable.defaults().pad(5);



            Texture skinBall = new Texture(Gdx.files.internal(p.getSkinBallPath()));
            skinTextures.add(skinBall);

            Label labelNome = new Label(p.getUsername(), skinLabel, "labelPixelyWhite32");
            labelNome.setFontScale(1f);
            labelNome.setColor(1, 1, 1, 1);

            Image skinImage = new Image(skinBall);

            Table playerInfoTable = new Table();
            playerInfoTable.add(labelNome).center().row();
            playerInfoTable.add(skinImage).center();

            playerTable.add(playerInfoTable).center().pad(10);

            if (p.getStars() > 0) 
            {
                int index = r.nextInt(0,pointsPaths.length);
                Animation<TextureRegion> ponto_inicio = Auxiliaries.animationFromFiles(pointsPaths[index] + "/inicio.png", pointsPaths[index] + "/inicio.json");
                Animation<TextureRegion> ponto_meio = Auxiliaries.animationFromFiles(pointsPaths[index] + "/meio.png", pointsPaths[index] + "/meio.json");
                for (int i = 0; i < p.getStars(); i++) 
                {
                    Animation<TextureRegion> ponto = (i == 0) ? ponto_inicio : ponto_meio;

                    ponto.setPlayMode(PlayMode.NORMAL);
                    AnimatedPoint star = new AnimatedPoint(ponto, i * 3.5f);
                    playerTable.add(star.image).pad(0.1f);
                    animatedPoints.add(star);
                }
                playerTable.row(); 
            }

            mainTable.add(playerTable).padBottom(20).row();
        }




        topTable.top().center().padTop(10).padBottom(20); 

        root.add(topTable).expandX().padTop(10).row();
        root.add(mainTable).expand().top();

        stage.addActor(imageBG);     
        stage.addActor(root);   

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0.1f, 0, 1, true);
        stage.act(delta);
        stage.draw();

        elapsedTime += delta;
        for (AnimatedPoint point : animatedPoints) 
        {
            point.update(elapsedTime);
        }
    }

    @Override
    public void resize(int width, int height) {
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
        for(Texture t: numTextures)
        {
            t.dispose();
        }

        for(Texture t: skinTextures)
        {
            t.dispose();
        }

        textureBG.dispose();
        textureRound.dispose();
    }

}

class AnimatedPoint 
{
    final Image image;
    final Animation<TextureRegion> animation;
    final float appearTime; 

    AnimatedPoint(Animation<TextureRegion> animation, float appearTime) 
    {
        this.animation = animation;
        this.appearTime = appearTime;
        this.image = new Image(new TextureRegionDrawable(animation.getKeyFrame(0)));
        this.image.setVisible(false); 
    }

    void update(float totalElapsed) 
    {
        if (totalElapsed >= appearTime) 
        {
            image.setVisible(true);
            float localTime = totalElapsed - appearTime;
            image.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(localTime, false)));
        }
    }
}
