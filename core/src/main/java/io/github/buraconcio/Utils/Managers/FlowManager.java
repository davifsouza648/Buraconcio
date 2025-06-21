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

    public FlowManager() {
        this.isHost = Constants.isHosting();
        if (isHost) {
            startPlayPhase();
        }
    }

    public static synchronized FlowManager getInstance() {
        if (instance == null) {
            instance = new FlowManager();
        }

        return instance;
    }

    private void startPlayPhase() {
        changePhase("play");
        startHostTimer(GameManager.getInstance().getPlayTime(), new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {

                if (PlayerManager.getInstance().areAllBallsDead()) {
                    stopAndNotify();

                    startPointsPhase();
                }

            }

            @Override
            public void finish() {
                startPointsPhase();
            }
        });
    }

    private void startSelectObstaclePhase() {
        changePhase("select_obj");

        flag = true;

        delayToClear = 0;
        startHostTimer(GameManager.getInstance().getSelectTime(), new CountdownTimer.TimerListener() {

            @Override
            public void tick(int remainingSecs) {

                delayToClear++;

                if((PlayerManager.getInstance().hasEveryoneClaimed() || delayToClear >= GameManager.getInstance().getTimeToClear())&& flag){
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

    private void startPointsPhase() {
        changePhase("show_points");
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

    private void winPhase() {
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

    private void changePhase(String phase) {
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
}
