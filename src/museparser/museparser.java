package museparser;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class museparser extends PApplet {
    int PORT = 9000;
    OscP5 oscP5;

    public void settings() {
        size(20, 20);

        oscP5 = new OscP5(this, PORT);
    }

    public void draw() {
        background(0);
    }

    public void oscEvent(OscMessage msg) {
        if (msg.checkAddrPattern("/muse/elements/alpha_absolute")) {
            for (int i = 0; i < 4; i++) {
                System.out.print("alpha (" + i + "): " + msg.get(i).floatValue() + "\n");
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("museparser.museparser");
    }
}
