package dusttunnel;

import themidibus.MidiBus;

import java.util.ArrayList;
import java.util.Random;

public class NoteEngine {
    MidiBus midiBus; // shared throughout program

    private int _channel;

    private int[] _scale = {0, 4, 7, 11, 14, 18, 21}; // maj9#11 13
    private int _rootOffset = 0;
    private int _octaveOffset = 0;
    private int MAX_VELOCITY_ALLOWED = 80;
    private float NOTE_ON_PROBABILITY = 0.008f;
    private float NOTE_OFF_PROBABILITY = 0.05f;
    private float ODD_NOTE_PROBABILITY = 0.05f;

    private ArrayList<Integer> _onNotes = new ArrayList<>();
    private Random random = new Random();

    public NoteEngine(int channel, MidiBus midiBus) {
        _channel = channel;
        this.midiBus = midiBus;
    }

    /**
     * randomly play notes
     */
    public void update() {
        if (Math.random() < NOTE_ON_PROBABILITY) {
            playNote();
        }

        if (Math.random() < NOTE_OFF_PROBABILITY) {
            stopNote();
        }
    }

    public void reset() {
        for (int i = 0; i < getOnNotes().size(); i++) {
            stopNote();
        }
    }

    private int getChannel() {
        return _channel;
    }

    private int[] getScale() {
        return _scale;
    }

    public void setRootOffset(int offset) {
        _rootOffset = offset;
    }

    private int getOctaveOffset() {
        return _octaveOffset;
    }

    private int getRootOffset() {
        return _rootOffset;
    }

    public void setOctaveOffset(int offset) {
        _octaveOffset = offset;
    }

    private int getNoteNumber() {
        int[] scale = getScale();
        int pitch = scale[random.nextInt(scale.length)];
        int note = pitch + getRootOffset() + (12 * getOctaveOffset());
        if (Math.random() > ODD_NOTE_PROBABILITY) {
            note += 1;
        }
        return note;
    }

    /**
     * make a reasonable velocity based on muze value
     * @return velocity
     */
    private int getVelocity() {
        int velocity = (int) Math.floor(MAX_VELOCITY_ALLOWED * Math.random());
        return velocity;
    }

    public ArrayList<Integer> getOnNotes() {
        return _onNotes;
    }

    private void updateOnNotes(int noteNumber) {
        getOnNotes().add(noteNumber);
    }

    private boolean hasOnNotes() {
        return getOnNotes().size() != 0;
    }

    private int getOnNoteIndex() {
        int randomIndex = random.nextInt(getOnNotes().size());
        return randomIndex;
    }


    /**
     * get a note, send note on message, delay and note off message
     */
    public void playNote() {
        int noteNumber = getNoteNumber();
        int channel = getChannel();
        int velocity = getVelocity();

        midiBus.sendNoteOn(channel, noteNumber, velocity); // Send a Midi noteOn
        updateOnNotes(noteNumber);
        System.out.print("on notes: " + getOnNotes() + "\n");
    }

    public void stopNote() {
        if (hasOnNotes()) {
            int channel = getChannel();
            int noteIndex = getOnNoteIndex();
            int noteNumber = getOnNotes().get(noteIndex);
            midiBus.sendNoteOff(channel, noteNumber, 0); // Send a Midi noteOff
            getOnNotes().remove(noteIndex);
        }
    }
}
