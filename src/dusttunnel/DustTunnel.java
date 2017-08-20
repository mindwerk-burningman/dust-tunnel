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
    int NUM_NOTE_ENGINES = 4;
    int NUM_CONTROLLER_ENGINES = 4;
    int CONTROLLER_NUMBER_OFFSET = 9;

    NoteEngine[] noteEngines = new NoteEngine[NUM_NOTE_ENGINES];
    ControllerEngine[] controllerEngines = new ControllerEngine[NUM_CONTROLLER_ENGINES];
    AttentionEngine attentionEngine;

    boolean isInitialized = false;

    public void setup() {
        noLoop();

//        oscP5 = new OscP5(this, PORT, OscP5.TCP); // from file readout
        oscP5 = new OscP5(this, PORT); // from headset

        MidiBus.list();
        midiBus = new MidiBus(this, -1, MIDI_BUS_CHANNEL);
        initNoteEngines();
        initControllerEngines();
        initAttentionEngine();

        // avoid getting osc messages before initialized
        isInitialized = true;
    }

    private void initNoteEngines() {
        for (int i = 0; i < noteEngines.length; i++) {
            noteEngines[i] = new NoteEngine(i + 1, midiBus);
        }
    }

    private void initControllerEngines() {
        for (int i = 0; i < controllerEngines.length; i++) {
            controllerEngines[i] = new ControllerEngine(0, i + CONTROLLER_NUMBER_OFFSET, midiBus);
        }
    }

    private void initAttentionEngine() {
         attentionEngine = new AttentionEngine(models);
    }

    public void settings() {
        size(400, 400);
    }

    public void draw() {
        background(0);
        drawModels();
//        println(attentionEngine.isAttentionGrowing());
//        println(frameRate);
    }

    private void drawModels() {
        int bandWidth = width / models.length;
        float b = 0;
        for (int i = 0; i < models.length; i++) {
            float x = bandWidth * i;
            float last = models[i].getLast();
            float y = height - (height * last);
            float percentHeight = y / height;
            float r = 255 - (255 * percentHeight);
            float g = 255 - r;
            fill(r + 40, g, b);
            rect(x, y, bandWidth, height);
        }
    }

    private void updateSystems(MuseModel model, OscMessage msg, int modelIndex) {
        float value = model.getValue(msg);
        controllerEngines[modelIndex].update(value);

        for (NoteEngine noteEngine : noteEngines) {
            noteEngine.update();
        }
        attentionEngine.update();
        redraw();
    }

    /**
     * Entry point... all events are based on a valid osc message
     * rather than frame rate
     * @param msg from muse
     */
    public void oscEvent(OscMessage msg) {
        if (isInitialized) {
            update(msg);
        }
    }

    private void update(OscMessage msg) {
        for (int i = 0; i < models.length; i++) {
            if (models[i].isValidMsg(msg)) {
                updateSystems(models[i], msg, i);
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("dusttunnel.DustTunnel");
    }
}
