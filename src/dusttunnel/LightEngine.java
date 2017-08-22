package dusttunnel;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import java.util.Timer;
import java.util.TimerTask;

public class LightEngine {

    OscP5 _oscP5;
    NetAddress remoteAddress;
    Timer timer;

    private float _valueK = 1;
    private int _status = 0;

    private String _addressPattern;
    final String HOST = "10.0.1.5";
    final int PORT = 9000;

    final long FADE_OUT_RATE = 500L;
    final float FADE_OUT_AMOUNT = 0.05f;

    public LightEngine(OscP5 oscP5) {
//        super(models);
//        _models = models;
        _oscP5 = oscP5;
        remoteAddress = new NetAddress(HOST, PORT);
    }

    public void update(MuseModel model) {
        if (model.getAddressPattern() == "/muse/elements/alpha_absolute") {
            send(model.getUserValue(), "/sean/alpha");
        }

        if (model.getAddressPattern() == "/muse/elements/gamma_absolute") {
            send(model.getUserValue(), "/sean/beta");
        }
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
//        float value = getLast() - FADE_OUT_AMOUNT;
//        setLast(value);
//        if (value <= 0) {
//            timer.cancel();
//            send(0, 0.0f);
//        }
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

    private String getAddressPattern() {
        return _addressPattern;
    }

    public void setAddressPattern(String addressPattern) {
        _addressPattern = addressPattern;
    }

    private void send(float value, String addressPattern) {
        OscMessage msg = new OscMessage("/sean/alpha");
        msg.add(value);
        _oscP5.send(msg, remoteAddress);
    }
}
