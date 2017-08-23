package dusttunnel;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TriggerEngine {
    AttentionEngine _engine;

    private float _last;
    private float _curr;
    final float THRESHOLD = 0.5f;
    final long TRIGGER_TIME = 1000L;
    Timer timer = new Timer();
    Random random = new Random();
    private boolean isAboveThreshold = false;
    private boolean isTimerActive = false;

    public TriggerEngine(AttentionEngine engine) {
        _engine = engine;
    }

    private AttentionEngine getEngine() {
        return _engine;
    }

    public void update() {
        float value = getEngine().getUserValue();
        if (value > THRESHOLD) {
            isAboveThreshold = true;

            if (!isTimerActive) {
                isTimerActive = true;
                startTimer();
                print("timer started");
            }
        } else {
            isAboveThreshold = false;

            if (isTimerActive) {
                isTimerActive = false;
                timer.cancel();
                print("timer stopped");
            }
        }
    }

    public void reset() {
        timer.cancel();
        isAboveThreshold = false;
        isTimerActive = false;
    }

    // if above threshold for more than TRIGGER TIME
    // start trigger action
    private void triggerTest() {
        print("trigger test");
        if (isAboveThreshold && isTimerActive) {
            triggerAction();
            print("ACTION");
        }
    }

    private void startTimer() {
        if (!isTimerActive) {
            TimerTask task = new TimerTask() {
                public void run() {
                    triggerTest();
                }
            };

            long delay  = 0L;
            long period = TRIGGER_TIME;
            timer.schedule(task, delay, period);
        }
    }

    private void print(String msg) {
        System.out.print(msg + "\n");
    }

    // pick from actions and trigger one
    public void triggerAction() {
        // bass
    }
}
