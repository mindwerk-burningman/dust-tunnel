package dusttunnel;

import themidibus.MidiBus;

import java.util.ArrayList;

public class NoteEngine {
    MidiBus midiBus;
    private int _channel;
    private int[] _scale = {49, 51, 52, 54, 56, 57, 59}; // default dorian
    private int MAX_VELOCITY_ALLOWED = 127;
    private ArrayList<Integer> _onNotes = new ArrayList<>();

    public NoteEngine(int channel) {
        _channel = channel;
        initMidiBus();
    }

    public NoteEngine(int[] scale) {
        _scale = scale;
        initMidiBus();
    }

    private void initMidiBus() {
        midiBus = new MidiBus(this, -1, getChannel());
    }

    private int getChannel() {
        return _channel;
    }

    private int[] getScale() {
        return _scale;
    }

    private int getNoteNumber() {
        int index = (int) Math.floor(Math.random() * getScale().length - 1);
        return getScale()[index];
    }

    /**
     * make a reasonable velocity based on muze value
     * @param value MuseModel value between 0 & 1
     * @return velocity
     */
    private int getVelocity(float value) {
        int velocity = (int) Math.floor(MAX_VELOCITY_ALLOWED * value);
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
        int size = getOnNotes().size();
        return (int) Math.floor(Math.random() * size - 1);
    }


    /**
     * get a note, send note on message, delay and note off message
     */
    public void playNote(float value) {
        int noteNumber = getNoteNumber();
        int channel = getChannel();
        int velocity = getVelocity(value);

        midiBus.sendNoteOn(channel, noteNumber, velocity); // Send a Midi noteOn
        updateOnNotes(noteNumber);
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
