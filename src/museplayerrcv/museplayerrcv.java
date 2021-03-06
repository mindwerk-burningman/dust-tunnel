package museplayerrcv;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class museplayerrcv extends PApplet {
    int PORT = 9000;
    OscP5 oscP5;

    public void settings() {
        size(200, 200);

        // create an oscP5 instance using TCP listening on PORT
        oscP5 = new OscP5(this, PORT, OscP5.TCP);
    }

    public void draw() {
        background(0);
    }

    void oscEvent(OscMessage msg) {
        if (msg.checkAddrPattern("/muse/elements/alpha_absolute")) {
            for (int i = 0; i < 4; i++) {
                System.out.print("alpha (" + i + "): " + msg.get(i).floatValue() + "\n");
            }
        }
    }

    public static void main(String... args) {
        PApplet.main("museplayerrcv.museplayerrcv");
    }
}
