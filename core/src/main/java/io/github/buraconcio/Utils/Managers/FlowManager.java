package io.github.buraconcio.Utils.Managers;

import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Common.CountdownTimer;

public class FlowManager {

    private CountdownTimer timer;
    private boolean isHost, flag;
    private static FlowManager instance;
    int delayToClear = 0; //delay para limpar os obstaculos nao selecionados
    private int round = 0;
    private boolean playPhaseHandled = false;


    public FlowManager() {
    }

    public void start(){
        this.isHost = Constants.isHosting();
        if (isHost) {
            startPlayPhase();
        }
    }

    private void startPlayPhase() {
        System.out.println("INICIANDO FASE: PLAY");
        changePhase("play");
        round++;
        PlayerManager.getInstance().getLocalPlayer().resetStrokes();
        playPhaseHandled = false;

        startHostTimer(GameManager.getInstance().getPlayTime(), new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {
                if (!playPhaseHandled && PlayerManager.getInstance().areAllBallsDead()) {
                    playPhaseHandled = true;
                    stopAndNotify();

                    startPointsPhase();
                }

            }

            @Override
            public void finish()
            {
                if (!playPhaseHandled) {
                playPhaseHandled = true;
                startPointsPhase();
            }
            }
        });
    }

    public void startSelectObstaclePhase() {
        changePhase("select_obj");

        flag = true;
        PlayerManager.getInstance().getLocalPlayer().resetStrokes();

        delayToClear = 0;
        startHostTimer(GameManager.getInstance().getSelectTime(), new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {

                delayToClear++;

                if((PlayerManager.getInstance().hasEveryoneClaimed() || delayToClear >= GameManager.getInstance().getTimeToClear()) && flag){
                    ConnectionManager.getInstance().getServer().sendString(Message.Type.CLEAR_UNCLAIMED, "");
                    flag = false;
                }

                if (PlayerManager.getInstance().hasEveryonePlaced()) {
                    stopAndNotify();
                    startPlayPhase();
                }

            }

            @Override
            public void finish() {

                for (Player p : PlayerManager.getInstance().getAllPlayers()) {

                    if (p.getSelectedObstacle() != null && p.getSelectedObstacle().canPlace()) {

                        p.placeObstacle();
                    }
                }

                startPlayPhase();
            }
        });
    }

    public void startPointsPhase()
    {
        System.out.println("INICIANDO FASE: POINTS");
        System.out.println("Get win: " + PlayerManager.getInstance().getWin());
        System.out.println("Player: " + PlayerManager.getInstance().getLocalPlayer().getStars());
        if(PlayerManager.getInstance().getWin()) //Checa se alguém ganhou para ir para a victory screen
        {
            winPhase();
            return;
        }

        changePhase("show_points");
        PlayerManager.getInstance().getLocalPlayer().resetStrokes();

        System.out.println("SHOW POINTS");
        startHostTimer(GameManager.getInstance().getPointsTime(), new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {
            }

            @Override
            public void finish() {
                startSelectObstaclePhase();
            }
        });
    }

    public void winPhase() {
        changePhase("show_win");
        stopTimer();
    }

    private void startHostTimer(int seconds, CountdownTimer.TimerListener listener) {
        stopTimer();
        timer = new CountdownTimer(seconds, listener);
        timer.start();
    }

    private void sendTimerStopMessage() {
        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendString(Message.Type.TIMER_STOP, "");
        }
    }

    public void onReceivePhaseChange(String phaseStr) {
        stopTimer();

        int phaseTime = switch (phaseStr) {

            case "play" -> GameManager.getInstance().getPlayTime();

            case "select_obj" -> GameManager.getInstance().getSelectTime();

            case "show_points" -> GameManager.getInstance().getPointsTime();

            case "show_win" -> GameManager.getInstance().getWinTime();

            default -> 0;
        };

        if (phaseTime > 0) {
            startClientTimer(phaseTime);
        }
    }

    private void startClientTimer(int seconds) {
        stopTimer();

        System.out.println("timando");
        timer = new CountdownTimer(seconds, new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {
                // se quisermos fazer uma label mudando no topo da tela usando os segundos é
                // aqui
            }

            @Override
            public void finish() {
            }
        });
        timer.start();
    }

    private void changePhase(String phase)
    {
        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendString(Message.Type.PHASE_CHANGE, phase);
        }
    }

    private void stopAndNotify() {
        stopTimer();
        sendTimerStopMessage();
    }

    public void onReceiveTimerStop() {
        stopTimer();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public int getRound()
    {
        return this.round;
    }
}
