package dusttunnel;

import themidibus.ControlChange;
import themidibus.MidiBus;

import java.util.Timer;
import java.util.TimerTask;

public class ControllerEngine {

    MidiBus midiBus;
    private MuseModel _model;
    private Timer timer = new Timer();

    private int _channel;
    private int _controllerNumber;
    private int _last;
    private int _maxValue = 127;
    private int _minValue = 0;
    private boolean _isInverse = false;

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

    private boolean getIsInverse() {
        return _isInverse;
    }

    private void setIsInverse(boolean isInverse) {
        _isInverse = isInverse;
    }

    private int getMaxValue() {
        return _maxValue;
    }

    public void setMaxValue(int value) {
        _maxValue = value;
    }

    private int getMinValue() {
        return _minValue;
    }

    public void setMinValue(int value) {
        _minValue = value;
    }

    public void setModel(MuseModel model) {
        _model = model;
    }

    private MuseModel getModel() {
        return _model;
    }

    // 0 - 1 => min - max
    private int getValue(float userValue) {
        int value = (int) Math.floor(userValue * getMaxValue() - getMinValue()) + getMinValue();
        if (getIsInverse()) {
            return getMaxValue() - value;
        }
        return value;
    }

    // 0 - 1
    public void update(MuseModel model) {
        if (model.getAddressPattern() == getModel().getAddressPattern()) {
            int value = getValue(model.getUserValue());
            ControlChange controlChange = new ControlChange(getChannel(), getControllerNumber(), value);
            midiBus.sendControllerChange(controlChange);
            setLast(value);
        }
    }

    private void fadeOut() {
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
                fadeOut();
            }
        };

        long delay  = 0L;
        long period = 500L;
        timer.scheduleAtFixedRate(resetTask, delay, period);
    }
}
