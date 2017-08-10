package osctomidicc;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.ControlChange;
import themidibus.MidiBus;
import themidibus.Note;

public class osctomidicc extends PApplet {
    OscP5 oscP5;
    NetAddress myRemoteLocation;
    MidiBus midiBus;
    String channel = "MIDI Monitor (Untitled)";
//    String channel = "DustTunnel";

    public void settings() {
        size(200,200);
    }

    public void setup() {
        frameRate(25);
        /** start oscP5, listening for incoming messages at port 12000 */
        oscP5 = new OscP5(this,23456);
        myRemoteLocation = new NetAddress("127.0.0.1",23456);

        MidiBus.list();

        midiBus = new MidiBus(this, -1, channel); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.
    }


    /**
     * start the app
     */
    public void draw() {
        background(0);
    }

    public void mousePressed() {
        OscMessage myMessage = new OscMessage("/test");

        myMessage.add(mouseY);
        myMessage.add(42);
        myMessage.add(84);

        oscP5.send(myMessage, myRemoteLocation);
    }

    public void playNote() {
        int channel = 1;
        int pitch = 84;
        int velocity = 127;
        Note note = new Note(channel, pitch, velocity);

        midiBus.sendNoteOn(note); // Send a Midi noteOn
        delay(200);
        midiBus.sendNoteOff(note); // Send a Midi noteOff
    }

    public void startCC(int mouseY) {
        int number = 16;
        int channel = 0;
        float rangePercentage = (float) mouseY / (float) height;
        int value = 127 - (int) Math.floor(rangePercentage * 127);
        ControlChange change = new ControlChange(channel, number, value);

        midiBus.sendControllerChange(change); // Send a controllerChange
    }

    /** incoming osc message are forwarded to the oscEvent method. */
    public void oscEvent(OscMessage theOscMessage) {
        /** print the address pattern and the typetag of the received OscMessage */
        println("### received an osc message.");
        println(theOscMessage);
//        playNote();
        int mouseY = theOscMessage.get(0).intValue();
        startCC(mouseY);
    }

    public static void main(String... args) {
        PApplet.main("osctomidicc.osctomidicc");
    }
}
