package oscutil;

import processing.core.*;

/**
 * binds an OSCEvent with a property in order to apply the value
 */
public class OSCBinding {
  public OSCEvent event;
  public Configurable prop;
  public float min;
  public float max;

  public OSCBinding(OSCEvent event, Configurable prop, float min, float max) {
    this.event = event;
    this.prop = prop;
    this.min = min;
    this.max = max;
  }

  public OSCBinding(OSCEvent event, Configurable prop) {
    this.event = event;
    this.prop = prop;
  }

  public void trigger(OSCEvent event) {
    if (this.prop.fVal != 0.0f) {
      fAction(event.fVal);
    } else if (this.prop.sVal != null) {
      // toggles always pass bools
      sAction(this.event.sVal);
    } else if (this.prop.iVal != 0) {
      // faders always pass floats
      iAction(event.fVal);
    } else {
      bAction(event.bVal);
    }
  }

  public void iAction(float fVal) {
    this.prop.iVal = PApplet.floor(PApplet.map(fVal, 0, 1, this.min, this.max));
  }

  public void fAction(float fVal) {
    this.prop.fVal = PApplet.map(fVal, 0, 1, this.min, this.max);
  }

  public void sAction(String sVal) {
    PApplet.println("sVal: " + sVal);
    this.prop.sVal = sVal;
  }

  public void bAction(boolean bVal) {
    this.prop.bVal = bVal;
  }
}
