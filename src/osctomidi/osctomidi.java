package osctomidi;

import processing.core.PApplet;

public class osctomidi extends PApplet {
//    String channe = "MIDI Monitor (Untitled)";
    String channel = "DustTunnel";

    public void settings() {
        size(200,200);
    }

    public void setup() {}


    /**
     * start the app
     */
    public void draw() {
        background(0);
    }


    public static void main(String... args) {
        PApplet.main("osctomidiosctomidi.osctomidi");
    }
}
