package dusttunnel;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

public class DustTunnel extends PApplet {

    // signal options
    final String ALPHA_ADDRESS_PATTERN = "/muse/elements/alpha_absolute";
    final String BETA_ADDRESS_PATTERN = "/muse/elements/beta_absolute";
    final String GAMMA_ADDRESS_PATTERN = "/muse/elements/gamma_absolute";
    final String THETA_ADDRESS_PATTERN = "/muse/elements/theta_absolute";
    final String[] VALID_ADDRESS_PATTERNS = {ALPHA_ADDRESS_PATTERN, BETA_ADDRESS_PATTERN, GAMMA_ADDRESS_PATTERN, THETA_ADDRESS_PATTERN};

    // muse model
    MuseModel alpha = new MuseModel(ALPHA_ADDRESS_PATTERN);
    MuseModel beta = new MuseModel(BETA_ADDRESS_PATTERN);
    MuseModel gamma = new MuseModel(GAMMA_ADDRESS_PATTERN);
    MuseModel theta = new MuseModel(THETA_ADDRESS_PATTERN);
    MuseModel[] models = { alpha, beta, gamma, theta };

    // osc
    OscP5 oscP5;
    int PORT = 9000;

    // midi
    MidiBus midiBus;
//    String MIDI_BUS_CHANNEL = "MIDI Monitor (Untitled)";
    String MIDI_BUS_CHANNEL = "DustTunnel";

    NoteEngine channel1;
    NoteEngine channel2;
    NoteEngine channel3;
    NoteEngine[] channels = {channel1, channel2, channel3};

    ControllerEngine controller16;
    ControllerEngine controller17;
    ControllerEngine controller18;
    ControllerEngine controller19;
    ControllerEngine[] controllers;

    boolean isInitialized = false;

    public void setup() {
        frameRate(30);
//        oscP5 = new OscP5(this, PORT, OscP5.TCP); // from file readout
        oscP5 = new OscP5(this, PORT); // from headset

        MidiBus.list();
        midiBus = new MidiBus(this, -1, MIDI_BUS_CHANNEL);
        channel1 = new NoteEngine(0, midiBus);
        channel2 = new NoteEngine(1, midiBus);
        channel3 = new NoteEngine(2, midiBus);

        controller16 = new ControllerEngine(0, 16, midiBus);
        controller17 = new ControllerEngine(1, 17, midiBus);
        controller18 = new ControllerEngine(2, 18, midiBus);
        controller19 = new ControllerEngine(3, 19, midiBus);
        controllers = new ControllerEngine[4];
        controllers[0] = controller16;
        controllers[1] = controller17;
        controllers[2] = controller18;
        controllers[3] = controller19;

        // ovoid getting osc messages before initialized
        isInitialized = true;
    }

    public void settings() {
        size(20, 20);
    }

    public void draw() {
        background(0);
        channel1.update();
        channel2.update();
        channel3.update();
    }

    public void oscEvent(OscMessage msg) {
        if (isInitialized) {
            filterMessages(msg);
        }
    }

    private void filterMessages(OscMessage msg) {
        for (int i = 0; i < models.length; i++) {
            if (models[i].isValidMsg(msg)) {
                controllers[i].update(models[i].getValue(msg));
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("dusttunnel.DustTunnel");
    }
}
