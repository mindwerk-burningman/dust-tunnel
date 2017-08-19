package musetest;

import processing.core.PApplet;
import oscP5.*;

public class musetest extends PApplet {
    public void settings() {
        size(200, 200);
    }

    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 20, 20);
    }

    public void oscEvent(event) {

    }

    public static void main(String... args) {
        PApplet.main("musetest.musetest");
    }
}