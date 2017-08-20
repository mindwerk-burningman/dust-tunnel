package dusttunnel;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class DustTunnel extends PApplet {

    // signal options
    final String ALPHA_ADDRESS_PATTERN = "/muse/elements/alpha_absolute";
    final String BETA_ADDRESS_PATTERN = "/muse/elements/beta_absolute";
    final String GAMMA_ADDRESS_PATTERN = "/muse/elements/gamma_absolute";
    final String THETA_ADDRESS_PATTERN = "/muse/elements/theta_absolute";
    final String[] VALID_ADDRESS_PATTERNS = {ALPHA_ADDRESS_PATTERN, BETA_ADDRESS_PATTERN, GAMMA_ADDRESS_PATTERN, THETA_ADDRESS_PATTERN};

    MuseModel alpha = new MuseModel(ALPHA_ADDRESS_PATTERN);
    MuseModel beta = new MuseModel(BETA_ADDRESS_PATTERN);
    MuseModel gamma = new MuseModel(GAMMA_ADDRESS_PATTERN);
    MuseModel theta = new MuseModel(THETA_ADDRESS_PATTERN);
    MuseModel[] models = { alpha, beta, gamma, theta };

    OscP5 oscP5;
    int PORT = 9000;

    public void settings() {
        size(20, 20);

//        oscP5 = new OscP5(this, PORT); // from headset
        oscP5 = new OscP5(this, PORT, OscP5.TCP); // from file readout
    }

    public void draw() {
        background(0);
    }

    public void oscEvent(OscMessage msg) {
//        filterMessages(msg);
        if (msg.checkAddrPattern(ALPHA_ADDRESS_PATTERN)) {
            println(alpha.getValue(msg));
        }
    }

    private void filterMessages(OscMessage msg) {
        for (MuseModel model : models) {
//            if (model.isValidMsg(msg)) {
//                println(model.getAddressPattern() + " : " + model.getValue(msg));
//            }
        }
    }

    public static void main(String... args) {
        PApplet.main("dusttunnel.DustTunnel");
    }
}
