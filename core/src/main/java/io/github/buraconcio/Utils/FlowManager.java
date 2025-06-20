package io.github.buraconcio.Utils;

import com.badlogic.gdx.scenes.scene2d.ui.Container;

import io.github.buraconcio.Main;
import io.github.buraconcio.Network.Message;
import io.github.buraconcio.Objects.Player;

public class FlowManager {

    private Main game;

    private CountdownTimer timer, delayTimer;

    public FlowManager(Main game) {
        this.game = game;
        startPlayPhase();
    }

    public void startPlayPhase() {
        changePhase("play");

        timer = new CountdownTimer(GameManager.getInstance().getPlayTime(), new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {

                // checar se as bolas estao mortas;

            }

            @Override
            public void finish() {
                startPointsPhase();
                PhysicsManager.getInstance().postRoundObstacles();
            }

        });

        timer.start();
    }

    public void startSelectObstaclePhase() {
        changePhase("select_obj");

        timer = new CountdownTimer(GameManager.getInstance().getSelectTime(), new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {

                //verificar se todos já colocaram seus obstaculos e puxar a finish()

                if(PlayerManager.getInstance().hasEveryonePlaced()){
                    System.out.println("TODOS COLOCARAM");
                    finish();
                }

            }

            @Override
            public void finish() {

                for (Player p : PlayerManager.getInstance().getAllPlayers()) {

                    if (p.getSelectedObstacle() != null && p.getSelectedObstacle().canPlace()) {

                        p.placeObstacle();
                    }
                }

                PlayerManager.getInstance().setEveryonePlaced(false);

                startPlayPhase();
                PhysicsManager.getInstance().preRoundObstacles();
            }

        });

        timer.start();
    }

    public void startPointsPhase() {

        changePhase("show_points");

        timer = new CountdownTimer(GameManager.getInstance().getPointsTime(), new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {
            }

            @Override
            public void finish() {
                startSelectObstaclePhase();
            }

        });

        timer.start();
    }

    public void winPhase() {
        changePhase("show_win");
    }

    public void changePhase(String str) {

        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendString(Message.Type.PHASE_CHANGE, str);
        }

    }

}
