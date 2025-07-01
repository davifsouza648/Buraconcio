package io.github.buraconcio.Utils.Managers;

import io.github.buraconcio.Network.TCP.Message;
import io.github.buraconcio.Objects.Game.Player;
import io.github.buraconcio.Utils.Common.Constants;
import io.github.buraconcio.Utils.Common.CountdownTimer;
import io.github.buraconcio.Utils.Managers.GameManager.PHASE;

public class FlowManager {

    private CountdownTimer clientTimer;
    private CountdownTimer hostTimer;
    private boolean isHost, flag;
    int delayToClear = 0; // delay para limpar os obstaculos nao selecionados
    private boolean playPhaseHandled = false;

    public FlowManager() {
    }

    public void start() {
        this.isHost = Constants.isHosting();
        if (isHost) {
            startPlayPhase();
        }
    }

    private void startPlayPhase() {
        changePhase("play");
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
            public void finish() {
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

                if ((PlayerManager.getInstance().hasEveryoneClaimed()
                        || delayToClear >= GameManager.getInstance().getTimeToClear()) && flag) {
                    flag = false;
                }

                if (PlayerManager.getInstance().hasEveryonePlaced()) {
                    ConnectionManager.getInstance().getServer().sendString(Message.Type.CLEAR_UNCLAIMED, "");
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

    public void startPointsPhase() {
        PlayerManager.getInstance().updateArrivalOrder();

        if (PlayerManager.getInstance().getWin()) // Checa se alguém ganhou para ir para a victory screen
        {
            winPhase();
            return;
        }

        changePhase("show_points");

        PlayerManager.getInstance().getLocalPlayer().resetStrokes();

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
        stopClientTimer();
        stopHostTimer();

        startHostTimer(GameManager.getInstance().getWinTime(), new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {
                // Se quiser, aqui pode atualizar HUD ou mostrar contagem
            }

            @Override
            public void finish() {
                // Fechar o jogo ao final
                System.out.println("Tempo de vitória encerrado");
                ConnectionManager.getInstance().getServer().stop();

            }
        });
    }

    private void startHostTimer(int seconds, CountdownTimer.TimerListener listener) {
        stopHostTimer();
        hostTimer = new CountdownTimer(seconds, listener);
        hostTimer.start();
    }

    private void sendTimerStopMessage() {
        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendString(Message.Type.TIMER_STOP, "");
        }
    }

    public void onReceivePhaseChange(String phaseStr) {
        stopClientTimer();

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
        stopClientTimer();

        clientTimer = new CountdownTimer(seconds, new CountdownTimer.TimerListener() {
            @Override
            public void tick(int remainingSecs) {
                if (GameManager.getInstance().phase == PHASE.PLAY) {
                    GameManager.getInstance().setArrivalTime(seconds - remainingSecs);

                    if (GameManager.getInstance().getPhysicsScreen().getHUD() != null) {
                        GameManager.getInstance().getPhysicsScreen().getHUD().updateClock(remainingSecs);
                    }
                }

                if (GameManager.getInstance().phase == PHASE.SELECT_OBJ) {
                    GameManager.getInstance().setArrivalTime(seconds - remainingSecs);

                    if (GameManager.getInstance().getPhysicsScreen().getHUD() != null) {
                        GameManager.getInstance().getPhysicsScreen().getHUD().updateClock(remainingSecs);
                    }
                }
            }

            @Override
            public void finish() {
            }
        });
        clientTimer.start();
    }

    private void changePhase(String phase) {
        if (Constants.isHosting()) {
            ConnectionManager.getInstance().getServer().sendString(Message.Type.PHASE_CHANGE, phase);
        }
    }

    private void stopAndNotify() {
        stopHostTimer();
        sendTimerStopMessage();
    }

    public void onReceiveTimerStop() {
        stopClientTimer();
    }

    private void stopClientTimer() {
        if (clientTimer != null) {
            clientTimer.stop();
            clientTimer = null;
        }
    }

    private void stopHostTimer() {
        if (hostTimer != null) {
            hostTimer.stop();
            hostTimer = null;
        }
    }
}
