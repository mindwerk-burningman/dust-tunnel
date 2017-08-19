package musetest;

import processing.core.PApplet;
import oscP5.*;

public class musetest extends PApplet {

    int recvPort = 5000;
    OscP5 oscP5;

    public void settings() {
        size(200, 200);
//        frameRate(60);
        oscP5 = new OscP5(this, recvPort);
    }

    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 20, 20);
    }

    void oscEvent(OscMessage msg) {
        System.out.println("### got a message " + msg);
        if (msg.checkAddrPattern("/muse/eeg")) {
            for (int i = 0; i < 4; i++) {
                System.out.print("EEG on channel " + i + ": " + msg.get(i).floatValue() + "\n");
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("musetest.musetest");
    }
}