package dusttunnel;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class DustTunnel extends PApplet {

    // signal options
    final String ALPHA_ADDRESS_PATTERN = "/muse/elements/alpha_absolute";
    final String BETA_ADDRESS_PATTERN = "/muse/elements/beta_absolute";
    final String GAMMA_ADDRESS_PATTERN = "/muse/elements/gamma_absolute";
    final String THETA_ADDRESS_PATTERN = "/muse/elements/theta_absolute";
    final String IS_GOOD = "/muse/elements/is_good";

    // muse model
    MuseModel alpha;
    MuseModel beta;
    MuseModel gamma;
    MuseModel theta;
    MuseModel[] models = new MuseModel[4];

    Timer timer = new Timer();
    private boolean shouldCheckHeadbandStatus = false;
    private long STATUS_CHECK_RATE = 1000L;

    // osc
    OscP5 oscP5;
    int PORT = 9000;

    // midi
    MidiBus midiBus;
//    String MIDI_BUS_CHANNEL = "MIDI Monitor (Untitled)";
    String MIDI_BUS_CHANNEL = "DustTunnel";
    int NUM_NOTE_ENGINES = 4;
    int NUM_CONTROLLER_ENGINES = 4;
    int CONTROLLER_NUMBER_OFFSET = 0;

    NoteEngine[] noteEngines = new NoteEngine[NUM_NOTE_ENGINES];
    ControllerEngine[] controllerEngines = new ControllerEngine[NUM_CONTROLLER_ENGINES];
    AttentionEngine attentionEngine;

    boolean shouldRecieveMessages = false; // control flow switch

    public void setup() {
        noLoop();
        init();
    }

    private void init() {
//        oscP5 = new OscP5(this, PORT, OscP5.TCP); // from file readout
        oscP5 = new OscP5(this, PORT); // from headset

        MidiBus.list();
        midiBus = new MidiBus(this, -1, MIDI_BUS_CHANNEL);
        initModels();
        initNoteEngines();
        initControllerEngines();
        initAttentionEngine();

        // avoid getting osc messages before initialized
        shouldRecieveMessages = true;

        TimerTask checkStatus = new TimerTask() {
            public void run() {
                shouldCheckHeadbandStatus = true;
            }
        };

        timer.scheduleAtFixedRate(checkStatus, STATUS_CHECK_RATE * 5, STATUS_CHECK_RATE);
    }

    private void initModels() {
        // muse model
        alpha = new MuseModel(ALPHA_ADDRESS_PATTERN, 0.75f);
        beta = new MuseModel(BETA_ADDRESS_PATTERN);
        gamma = new MuseModel(GAMMA_ADDRESS_PATTERN);
        theta = new MuseModel(THETA_ADDRESS_PATTERN, 0.5f);
        models[0] = alpha;
        models[1] = beta;
        models[2] = gamma;
        models[3] = theta;
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
        MuseModel[] models = {alpha, gamma};
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
        int bandWidth = width / (models.length + 1);
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

        // draw attention
        float x = bandWidth * models.length + 1;
        float last = attentionEngine.getLast();
        float y = height - (height * last);
        float percentHeight = y / height;
        float r = 255 - (255 * percentHeight);
        float g = 255 - r;
        fill(r + 40, g, b);
        rect(x, y, bandWidth, height);
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

    private void resetSystems() {
        // hasn't been reset yet
        for (NoteEngine noteEngine : noteEngines) {
            noteEngine.reset();
        }

        for (ControllerEngine controllerEngine : controllerEngines) {
            controllerEngine.reset();
        }
    }

    private void checkHeadsetStatus(OscMessage msg) {
        boolean isConnected = false;
        int[] statuses = new int[4];
        for (int i = 0; i < 4; i++) {
            statuses[i] = msg.get(i).intValue();
            if (statuses[i] == 1) {
                isConnected = true; // just need one
            }
        }

        // check to see if at least one connection
        if (isConnected) {
            shouldRecieveMessages = true;
        } else {
            resetSystems();
        }
        shouldCheckHeadbandStatus = false;
    }

    /**
     * Entry point... all events are based on a valid osc message
     * rather than frame rate
     * @param msg from muse
     */
    public void oscEvent(OscMessage msg) {
        if (shouldRecieveMessages) {
            update(msg);
        }

        if (shouldCheckHeadbandStatus && msg.checkAddrPattern(IS_GOOD)) {
            checkHeadsetStatus(msg);
            shouldCheckHeadbandStatus = false; // check once at interval
            println("checkin status");
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
