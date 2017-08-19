package museplayerrcv;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class museplayerrcv extends PApplet {
    int PORT = 9000;
    String ADDRESS = "localhost";
    OscP5 oscP5;
    NetAddress myLocation;

    public void settings() {
        size(200, 200);

        // create an oscP5 instance using TCP
        oscP5 = new OscP5(this, PORT, OscP5.TCP);
    }

    public void draw() {
        background(0);
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
        PApplet.main("museplayerrcv.museplayerrcv");
    }
}
