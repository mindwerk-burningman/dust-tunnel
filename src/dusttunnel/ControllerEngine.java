package dusttunnel;

import themidibus.ControlChange;
import themidibus.MidiBus;

import java.util.Timer;
import java.util.TimerTask;

public class ControllerEngine {

    MidiBus midiBus;

    private int _channel;
    private int _controllerNumber;
    private int _last;
    private int MAX_CONTROLLER_VALUE = 127;
    private int MAX_VALUE_DIFF = 10;
    private Timer timer = new Timer();

    public ControllerEngine(int channel, int controllerNumber, MidiBus midiBus) {
        _channel = channel;
        _controllerNumber = controllerNumber;
        this.midiBus = midiBus;
    }

    private int getChannel() {
        return _channel;
    }

    private int getControllerNumber() {
        return _controllerNumber;
    }

    private void setLast(int value) {
        _last = value;
    }

    private int getLast() {
        return _last;
    }

    private int smooth(int value) {
        int last = getLast();
        int diff = value - last;

        if (last > 0 && Math.abs(diff) > MAX_VALUE_DIFF) {
            return value + (int) Math.floor(diff / 2);
        }
        return value;
    }

    public void update(float value) {
        int controllerValue = (int) Math.floor(value * MAX_CONTROLLER_VALUE);
        int smoothed = smooth(controllerValue);
//        if (getControllerNumber() == 9) {
//            System.out.print(smoothed + "\n");
//        }
        ControlChange controlChange = new ControlChange(getChannel(), getControllerNumber(), smoothed);
        midiBus.sendControllerChange(controlChange);
        setLast(smoothed);
    }

    private void decLast() {
        int newLast = getLast() - 1;
        ControlChange controlChange = new ControlChange(getChannel(), getControllerNumber(), newLast);
        midiBus.sendControllerChange(controlChange);
        setLast(newLast);
        if (newLast == 0) {
            timer.cancel();
        }
    }

    public void reset() {
        TimerTask resetTask = new TimerTask() {
            public void run() {
                decLast();
            }
        };

        long delay  = 0L;
        long period = 500L;
        timer.scheduleAtFixedRate(resetTask, delay, period);
    }
}
