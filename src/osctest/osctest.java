package osctest;

import processing.core.*;

/**
 * Parses an incoming OSCMessage and returns an OSCEvent
 */
public class osctest extends PApplet {
  public void setup() {
    height = 200;
    width = 200;
  }

  public void draw() {
    ellipse(20, 20, height / 10, width / 10);
  }

  public static void main(String... args) {
    PApplet.main("osctest.osctest");
  }
}
