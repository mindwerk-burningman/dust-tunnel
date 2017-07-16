package oscutil;

import processing.core.*;
import oscP5.*;

/**
 * Parses an incoming OSCMessage and returns an OSCEvent
 */
public class OSCParser {
  public OSCEvent parse(OscMessage message) {
    float value;
    int number;
    String type;

    if (isFader(message)) {
      type = "fader";

      float faderValue = parseFaderValue(message);
      value = faderValue;
      PApplet.println("faderValue = " + faderValue);

      int faderNumber = parseControlNumber(message);
      number = faderNumber;
      PApplet.println("faderNumber = " + faderNumber);
      return new OSCEvent(type, number, value);
    }

    if (isToggle(message)) {
      type = "toggle";

      boolean isOn = parseToggleValue(message);
      PApplet.println("isOn = " + isOn);

      int toggleNumber = parseControlNumber(message);
      number = toggleNumber;
      PApplet.println("toggleNumber = " + toggleNumber);
      return new OSCEvent(type, number, isOn);
    }

    if (isPush(message)) {
      type = "push";

      int pushNumber = parseControlNumber(message);
      number = pushNumber;
      PApplet.println("pushNumber = " + pushNumber);

      boolean isOn = parsePushValue(message);
      PApplet.println("isOn = " + isOn);
      return new OSCEvent(type, number, isOn);
    }

    if (isRotary(message)) {
      type = "rotary";

      int rotaryNumber = parseControlNumber(message);
      number = rotaryNumber;
      PApplet.println("rotaryNumber = " + rotaryNumber);

      float rotaryValue = parseRotaryValue(message);
      value = rotaryValue;
      PApplet.println("rotaryValue = " + rotaryValue);
      return new OSCEvent(type, number, value);
    }

    return new OSCEvent("", 0, 0.0f);
  }

  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/

  /**
   * find out if message is from fader
   * @param OscMessage message
   * @return boolean
   */
  public boolean isFader(OscMessage message) {
    boolean isFader;
    String address = message.addrPattern();
    String faderMatchCase = "fader";
    String[] matches = PApplet.match(address, faderMatchCase);
    if (matches != null) {
      isFader = true;
    } else {
      isFader = false;
    }
    return isFader;
  }

  /**
   * find out if message is from toggle
   * @param OscMessage message
   * @return boolean
   */
  public boolean isToggle(OscMessage message) {
    boolean isToggle;
    String address = message.addrPattern();
    String faderMatchCase = "toggle";
    String[] matches = PApplet.match(address, faderMatchCase);
    if (matches != null) {
      isToggle = true;
    } else {
      isToggle = false;
    }
    return isToggle;
  }

  /**
   * find out if message is from rotary
   * @param OscMessage message
   * @return boolean
   */
  public boolean isRotary(OscMessage message) {
    boolean isRotary;
    String address = message.addrPattern();
    String faderMatchCase = "rotary";
    String[] matches = PApplet.match(address, faderMatchCase);
    if (matches != null) {
      isRotary = true;
    } else {
      isRotary = false;
    }
    return isRotary;
  }

  /**
   * find out if message is from push
   * @param OscMessage message
   * @return boolean
   */
  public boolean isPush(OscMessage message) {
    boolean isPush;
    String address = message.addrPattern();
    String faderMatchCase = "push";
    String[] matches = PApplet.match(address, faderMatchCase);
    if (matches != null) {
      isPush = true;
    } else {
      isPush = false;
    }
    return isPush;
  }

  /**
   * find out if message is from xy pad
   * @param OscMessage message
   * @return boolean
   */
  public boolean isXY(OscMessage message) {
    boolean isXY;
    String address = message.addrPattern();
    String faderMatchCase = "xy";
    String[] matches = PApplet.match(address, faderMatchCase);
    if (matches != null) {
      isXY = true;
    } else {
      isXY = false;
    }
    return isXY;
  }

  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/
  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/

  /**
   * Number parsers
   */

  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/

  /**
   * get the control number from address and convert to int
   * @param OscMessage message
   *               message.addrPattern
   *               is in the format "/[0-9]/fader[0-9]{1,}";
   * @return int controlNumber
   */
  public int parseControlNumber(OscMessage message) {
    String address = message.addrPattern();
    String[] parts = address.split("fader|toggle|rotary|xy|push");
    int controlNumber = Integer.parseInt(parts[parts.length - 1]);
    return controlNumber;
  }

  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/
  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/

  /**
   * value parsers
   */

  /*/~~~~~~~~~~~~~////~~~~~~~~~~~~~~////~~~~~~~~~~~~~/*/

  public float parseFaderValue(OscMessage message) {
    float faderValue = message.get(0).floatValue();
    return faderValue;
  }

  public float parseRotaryValue(OscMessage message) {
    float rotaryValue = message.get(0).floatValue();
    return rotaryValue;
  }

  public boolean parseToggleValue(OscMessage message) {
    boolean isOn;
    float ON = 1.0f;
    float toggleValue = message.get(0).floatValue();
    if (toggleValue == ON) {
      isOn = true;
    } else {
      isOn = false;
    }
    return isOn;
  }

  public boolean parsePushValue(OscMessage message) {
    boolean isOn;
    float ON = 1.0f;
    float pushValue = message.get(0).floatValue();
    if (pushValue == ON) {
      isOn = true;
    } else {
      isOn = false;
    }
    return isOn;
  }
}
