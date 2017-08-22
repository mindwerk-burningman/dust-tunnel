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
    private float _last;

    final String HOST = "10.0.1.5";
    final int PORT = 9000;

    final long FADE_OUT_RATE = 500L;
    final float FADE_OUT_AMOUNT = 0.05f;

    public LightEngine(OscP5 oscP5) {
        _oscP5 = oscP5;
        remoteAddress = new NetAddress(HOST, PORT);
    }

    private void setLast(float value) {
        _last = value;
    }

    public void update(MuseModel model) {
        if (model.getAddressPattern() == "/muse/elements/alpha_absolute") {
            float value = model.getUserValue();
            send(value, "/sean/alpha");
            setLast(value);
        }

        if (model.getAddressPattern() == "/muse/elements/gamma_absolute") {
            float value = model.getUserValue();
            send(value, "/sean/beta");
            setLast(value);
        }
    }

    public void reset() {
//        TimerTask resetTask = new TimerTask() {
//            public void run() {
//                fadeOut();
//            }
//        };
//
//        timer.scheduleAtFixedRate(resetTask, 0L, FADE_OUT_RATE);
        send(0.0f, "/muse/elements/touching_forehead");
    }

    private void fadeOut() {
        float value = _last - FADE_OUT_AMOUNT;
        setLast(value);
        if (value <= 0) {
            timer.cancel();
            send(0.0f, "/muse/elements/touching_forehead");
        }
    }

    /**
     * headset status
     */
    private void setStatus(int status) {
        _status = status;
    }

    private void send(float value, String addressPattern) {
        OscMessage msg = new OscMessage(addressPattern);
        msg.add(value);
        _oscP5.send(msg, remoteAddress);
    }
}
