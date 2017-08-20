package dusttunnel;

import themidibus.ControlChange;
import themidibus.MidiBus;

public class ControllerEngine {

    MidiBus midiBus;

    private int _channel;
    private int _controllerNumber;
    private int MAX_CONTROLLER_VALUE = 127;

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

    public void update(float value) {
        int controllerValue = (int) Math.floor(value * MAX_CONTROLLER_VALUE);
        ControlChange controlChange = new ControlChange(getChannel(), getControllerNumber(), controllerValue);
        midiBus.sendControllerChange(controlChange);
    }
}
