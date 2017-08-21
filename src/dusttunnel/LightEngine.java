package dusttunnel;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import java.util.Timer;
import java.util.TimerTask;

public class LightEngine extends AttentionEngine {

    OscP5 oscP5;
    NetAddress remoteAddress;
    Timer timer;

    private MuseModel[] _models;
    private float _valueK = 1;
    private int _status = 0;

    private String _addressPattern;
    private String HOST = "10.0.1.5";
    private int PORT = 9000;

    private long FADE_OUT_RATE = 500L;
    private float FADE_OUT_AMOUNT = 0.05f;

    public LightEngine(MuseModel[] models) {
        super(models);
        _models = models;
        remoteAddress = new NetAddress(HOST, PORT);
    }

    public void update() {
        setStatus(1);
        super.update();
        int status = getStatus();
        float value = getValue();
        send(status, value);
    }

    public void reset() {
        setStatus(0);

        TimerTask resetTask = new TimerTask() {
            public void run() {
                fadeOut();
            }
        };

        timer.scheduleAtFixedRate(resetTask, 0L, FADE_OUT_RATE);
    }

    private void fadeOut() {
        float value = getLast() - FADE_OUT_AMOUNT;
        setLast(value);
        if (value <= 0) {
            timer.cancel();
            send(0, 0.0f);
        }
    }

    /**
     * headset status
     */
    private void setStatus(int status) {
        _status = status;
    }

    private int getStatus() {
        return _status;
    }

    public void setValueK(float k) {
        _valueK = k;
    }

    private String getAddress() {
        return _addressPattern;
    }

    public void setAddressPattern(String addressPattern) {
        _addressPattern = addressPattern;
    }

    private void send(int status, float value) {
        String address = getAddress();
        OscMessage msg = new OscMessage(address);
        msg.add(status);
        msg.add(value);

        oscP5.send(msg, remoteAddress);
    }
}
