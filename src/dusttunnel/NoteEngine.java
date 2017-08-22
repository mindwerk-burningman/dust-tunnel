package dusttunnel;

import themidibus.MidiBus;

import java.util.ArrayList;
import java.util.Random;

public class NoteEngine {
    MidiBus midiBus; // shared throughout program
    MuseModel _model;
    private int _channel;

    private int[] _scale = {0, 4, 7, 11, 14, 18, 21}; // maj9#11 13
    private int _rootOffset = 0;
    private int _octaveOffset = 0;
    private int _notesAtATime = 1;
    private int _maxVelocityAllowed = 127;
    private int _minVelocityAllowed = 0;
    private float _noteOnProbability = 0.008f;
    private float _noteOffProbability = 0.05f;
    final float ODD_NOTE_PROBABILITY = 0.05f;
    final float NOTE_INC_DEC = 0.002f;

    private ArrayList<Integer> _onNotes = new ArrayList<>();
    private Random random = new Random();

    public NoteEngine(int channel, MidiBus midiBus, MuseModel model) {
        _channel = channel;
        this.midiBus = midiBus;
        _model = model;
    }

    /**
     * randomly play notes
     */
    public void update(MuseModel model) {
        if (model.getAddressPattern() == getModel().getAddressPattern()) {
            if (random.nextFloat() < _noteOnProbability) {
                playNote();
            }

            if (random.nextFloat() < _noteOffProbability) {
                stopNote();
            }
        }
    }

    public void reset() {
        for (int i = 0; i < getOnNotes().size(); i++) {
            stopNote();
        }
    }

    private MuseModel getModel() {
        return _model;
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

    public void setMaxVelocityAllowed(int maxVel) {
        _maxVelocityAllowed = maxVel;
    }

    public int getMaxVelocityAllowed() {
        return _maxVelocityAllowed;
    }

    public void setMinVelocityAllowed(int minVel) {
        _minVelocityAllowed = minVel;
    }

    public void setNoteOnProbability(float probability) {
        _noteOnProbability = probability;
    }

    public void setNoteOffProbability(float probability) {
        _noteOffProbability = probability;
    }

    public int getMinVelocityAllowed() {
        return _minVelocityAllowed;
    }

    public void incNoteOnProbability() {
        _noteOnProbability += NOTE_INC_DEC;
    }

    public void decNoteOnProbability() {
        _noteOnProbability -= NOTE_INC_DEC;
    }

    public void incNoteOffProbability() {
        _noteOnProbability += NOTE_INC_DEC;
    }

    public void decNoteOffProbability() {
        _noteOnProbability -= NOTE_INC_DEC;
    }

    private float getNoteOnProbability() {
        return _noteOnProbability;
    }

    private float getNoteOffProbability() {
        return _noteOffProbability;
    }

    /**
     * make a reasonable velocity based on muze value
     * @return velocity
     */
    private int getVelocity() {
        int velocity = _minVelocityAllowed + random.nextInt(_maxVelocityAllowed - _minVelocityAllowed + 1);
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

    public void setNotesAtAtime(int notesAtAtime) {
        _notesAtATime = notesAtAtime;
    }

    private int getNotesAtATime() {
        return _notesAtATime;
    }


    /**
     * get a note, send note on message, delay and note off message
     */
    public void playNote() {
        for (int i = 0; i < getNotesAtATime(); i++) {
            int noteNumber = getNoteNumber();
            int channel = getChannel();
            int velocity = getVelocity();

            midiBus.sendNoteOn(channel, noteNumber, velocity); // Send a Midi noteOn
            updateOnNotes(noteNumber);
        }
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
