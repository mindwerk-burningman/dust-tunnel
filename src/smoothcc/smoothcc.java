package smoothcc;

import processing.core.PApplet;
import themidibus.ControlChange;
import themidibus.MidiBus;

/**
 * Created by aric on 8/10/17.
 */
public class smoothcc extends PApplet {
    MidiBus midiBus;
    //    String channel = "MIDI Monitor (Untitled)";
    String channel = "DustTunnel";
    int highCutoff;
    int resonance;
    boolean shouldSendCC = false;
    int MAX_MIDI_VALUE = 127;

    public void settings() {
        size(200,200);
    }

    public void setup() {
        frameRate(50);
        MidiBus.list();
        midiBus = new MidiBus(this, -1, channel); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.
    }

    public void mousePressed() {
        shouldSendCC = !shouldSendCC;
        if (shouldSendCC) {
            println("START sending cc");
        } else {
            println("STOP sending cc");
        }
    }

    public void updateCCValues() {
        float cuttoffPercentage = (float) mouseX / (float) width;
        highCutoff = (int) Math.floor(cuttoffPercentage * MAX_MIDI_VALUE);

        float resPercentage = (float) mouseY / (float) height;
        resonance = MAX_MIDI_VALUE - (int) Math.floor(resPercentage * MAX_MIDI_VALUE);
    }

    public void sendCC() {
        if (shouldSendCC) {
            int number = 16;
            int channel = 0;

            ControlChange cuttoff = new ControlChange(channel, number, highCutoff);
            ControlChange res = new ControlChange(channel, 17, resonance);

            midiBus.sendControllerChange(cuttoff);
            midiBus.sendControllerChange(res);
        }
    }

    /**
     * start the app
     */
    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 10, 10);
        updateCCValues();
        sendCC();
    }

    public static void main(String... args) {
        PApplet.main("smoothcc.smoothcc");
    }
}
