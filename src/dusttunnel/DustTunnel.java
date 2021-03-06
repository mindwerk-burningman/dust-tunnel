package dusttunnel;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DustTunnel extends PApplet {
    final boolean IS_USING_HEADSET = false;
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

    // lower values = less noise
    final float ALPHA_K = 0.75f;
    final float BETA_K = 0.75f;
    final float GAMMA_K = 1.25f;
    final float THETA_K = 0.55f;

    final float NOTE_ON_PROBABILITY = 0.008f;
    final float NOTE_OFF_PROBABILITY = 0.05f;

    Timer timer = new Timer();
    private boolean shouldCheckHeadbandStatus = false;
    final long STATUS_CHECK_RATE = 1000L;

    // osc
    OscP5 oscP5;
    int PORT = 9000;

    // midi
    MidiBus midiBus;

    String MIDI_BUS_CHANNEL = "MIDI Monitor (Untitled)";
//    String MIDI_BUS_CHANNEL = "DustTunnel";

    int NUM_CONTROLLER_ENGINES = 4;
    int CONTROLLER_CHANNEL = 0;
    int NUM_NOTE_ENGINES = 3;

    // engines
    NoteEngine[] noteEngines = new NoteEngine[NUM_NOTE_ENGINES];
    ControllerEngine[] controllerEngines = new ControllerEngine[NUM_CONTROLLER_ENGINES];
    AttentionEngine attentionEngine;
    LightEngine lightEngine;
    TriggerEngine triggerEngine;

    boolean shouldReceiveMessages = false; // control flow switch

    public void setup() {
        noLoop();
        init();
    }

    private void init() {
        if (IS_USING_HEADSET) {
            oscP5 = new OscP5(this, PORT); // from headset
        } else {
            oscP5 = new OscP5(this, PORT, OscP5.TCP); // from file readout
        }

        MidiBus.list();
        midiBus = new MidiBus(this, -1, MIDI_BUS_CHANNEL);
        initModels();
        initNoteEngines();
        initControllerEngines();
        initAttentionEngine();
        initLightEngine();
        initTriggerEngine();

        // avoid getting osc messages before initialized
        shouldReceiveMessages = true;

        TimerTask checkStatus = new TimerTask() {
            public void run() {
                shouldCheckHeadbandStatus = true;
            }
        };

        timer.scheduleAtFixedRate(checkStatus, STATUS_CHECK_RATE * 5, STATUS_CHECK_RATE);
    }

    private void initModels() {
        // muse model
        alpha = new MuseModel(ALPHA_ADDRESS_PATTERN, ALPHA_K);
        beta = new MuseModel(BETA_ADDRESS_PATTERN, BETA_K);
        gamma = new MuseModel(GAMMA_ADDRESS_PATTERN, GAMMA_K);
        theta = new MuseModel(THETA_ADDRESS_PATTERN, THETA_K);
        models[0] = alpha;
        models[1] = beta;
        models[2] = gamma;
        models[3] = theta;
    }

    private void initNoteEngines() {
        NoteEngine bass = new NoteEngine(0, midiBus, gamma);
        bass.setOctaveOffset(1);
        bass.setMaxVelocityAllowed(40);
        bass.setNoteOnProbability(NOTE_ON_PROBABILITY * 0.5f);
        bass.setNoteOffProbability(NOTE_OFF_PROBABILITY * 0.5f);

        NoteEngine pads = new NoteEngine(1, midiBus, beta);
        pads.setOctaveOffset(3);
        pads.setNotesAtAtime(3);
        pads.setNoteOnProbability(NOTE_ON_PROBABILITY);
        pads.setNoteOffProbability(NOTE_OFF_PROBABILITY * 0.75f);

        NoteEngine sparkles = new NoteEngine(2, midiBus, alpha);
        sparkles.setOctaveOffset(6);
        sparkles.setNoteOnProbability(NOTE_ON_PROBABILITY * 1.25f);
        sparkles.setNoteOffProbability(NOTE_OFF_PROBABILITY * 1.25f);

        noteEngines[0] = bass;
        noteEngines[1] = pads;
        noteEngines[2] = sparkles;
    }

    private void initControllerEngines() {

        // bass
        ControllerEngine cutoff = new ControllerEngine(CONTROLLER_CHANNEL, 9, midiBus);
        cutoff.setMaxValue(25);
        cutoff.setModel(beta);

        // pads
        ControllerEngine glide = new ControllerEngine(CONTROLLER_CHANNEL, 7, midiBus);
        glide.setMaxValue(64);
        glide.setModel(theta);

        // sparkles
        ControllerEngine stereoVerb = new ControllerEngine(CONTROLLER_CHANNEL, 1, midiBus);
        stereoVerb.setModel(alpha);
        ControllerEngine verbTime = new ControllerEngine(CONTROLLER_CHANNEL, 2, midiBus);
        verbTime.setModel(gamma);

        controllerEngines[0] = cutoff;
        controllerEngines[1] = glide;
        controllerEngines[2] = stereoVerb;
        controllerEngines[3] = verbTime;
    }

    private void initAttentionEngine() {
        MuseModel[] models = {alpha, beta, gamma, theta};
        attentionEngine = new AttentionEngine(models);
    }

    private void initLightEngine() {
        lightEngine = new LightEngine(oscP5);
    }

    private void initTriggerEngine() {
        triggerEngine = new TriggerEngine(attentionEngine);
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
            float last = models[i].getUserValue();
            float y = height - (height * last);
            float percentHeight = y / height;
            float r = 255 - (255 * percentHeight);
            float g = 255 - r;
            fill(r + 40, g, b);
            rect(x, y, bandWidth, height);
        }

        // draw attention
        float x = bandWidth * models.length + 1;
        float last = attentionEngine.getUserValue();
        float y = height - (height * last);
        float percentHeight = y / height;
        float r = 255 - (255 * percentHeight);
        float g = 255 - r;
        fill(r + 40, g, b);
        rect(x, y, bandWidth, height);
    }

    private void updateSystems(MuseModel model) {
        for (ControllerEngine engine : controllerEngines) {
            engine.update(model);
        }

        for (NoteEngine noteEngine : noteEngines) {
            noteEngine.update(model);
        }

        attentionEngine.update();

//        lightEngine.update(model);

//        triggerEngine.update();

        redraw();
    }

    private void resetSystems() {
        // new root
        Random random = new Random();
        int rootOffset = random.nextInt(11) + 1;
        for (NoteEngine noteEngine : noteEngines) {
            noteEngine.reset();
            noteEngine.setRootOffset(rootOffset);
        }

        for (ControllerEngine controllerEngine : controllerEngines) {
            controllerEngine.reset();
        }

        lightEngine.reset();

        println("resetting");

//        triggerEngine.reset();
        // attention will go down with other CCs

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
            shouldReceiveMessages = true;
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
        if (shouldReceiveMessages) {
            update(msg);
        }

        if (shouldCheckHeadbandStatus && msg.checkAddrPattern(IS_GOOD)) {
            checkHeadsetStatus(msg);
            shouldCheckHeadbandStatus = false; // check once at interval
//            println("checkin status");
        }
    }

    private void update(OscMessage msg) {
        for (int i = 0; i < models.length; i++) {
            if (models[i].isValidMsg(msg)) {
                models[i].update(msg);
                updateSystems(models[i]);
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("dusttunnel.DustTunnel");
    }
}
