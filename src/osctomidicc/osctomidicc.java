package osctomidicc;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

public class osctomidicc extends PApplet {
    OscP5 oscP5;
    NetAddress myRemoteLocation;
    MidiBus midiBus;
    //    String channel = "MIDI Monitor (Untitled)";
    String channel = "DustTunnel";

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

        myMessage.add("test text");
        myMessage.add(new byte[]{0x00, 0x01, 0x10, 0x20});
        myMessage.add(new int[]{1, 2, 3, 4});

        oscP5.send(myMessage, myRemoteLocation);
    }

    public void playMidi() {
        int channel = 1;
        int pitch = 64;
        int velocity = 127;

        midiBus.sendNoteOn(channel, pitch, velocity); // Send a Midi noteOn
        delay(200);
        midiBus.sendNoteOff(channel, pitch, velocity); // Send a Midi noteOff
    }

    /** incoming osc message are forwarded to the oscEvent method. */
    public void oscEvent(OscMessage theOscMessage) {
        /** print the address pattern and the typetag of the received OscMessage */
        println("### received an osc message.");
        println(theOscMessage);
        playMidi();
    }

    public static void main(String... args) {
        PApplet.main("osctomidi.osctomidi");
    }
}
