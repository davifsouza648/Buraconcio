package io.github.buraconcio.Utils.Common;

import com.badlogic.gdx.utils.Timer;

public class CountdownTimer {

    public interface TimerListener {
        void tick(int remainingSecs);

        void finish();
    }

    private int time;
    private Timer.Task task;
    private TimerListener listener;

    public CountdownTimer(int time, TimerListener listener) {
        this.time = time;
        this.listener = listener;
    }

    public void start() {
        stop();
        task = new Timer.Task() {

            int last = time;

            @Override
            public void run() {

                if (last > 0) {

                    if (listener != null) {
                        listener.tick(last);
                    }

                    last--;

                } else {

                    if (listener != null) {
                        listener.finish();
                    }

                    cancel();
                }

            }
        };

        Timer.schedule(task, 0, 1);

    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void finish() {
        stop();

        if (listener != null) {
            listener.finish();
        }
    }

}
