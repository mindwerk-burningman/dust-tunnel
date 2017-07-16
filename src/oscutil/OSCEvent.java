package oscutil;

/**
 * holds reference to an OSC event via type and controller number
 */
public class OSCEvent {
  public String type;
  public int number;
  public float fVal;
  public String sVal;
  public boolean bVal;
  public int iVal;

  public OSCEvent(String type, int number, float fVal) {
    this.type = type;
    this.number = number;
    this.fVal = fVal;
  }

  public OSCEvent(String type, int number, String sVal) {
    this.type = type;
    this.number = number;
    this.sVal = sVal;
  }

  public OSCEvent(String type, int number, Boolean bVal) {
    this.type = type;
    this.number = number;
    this.bVal = bVal;
  }

  public OSCEvent(String type, int number, int iVal) {
    this.type = type;
    this.number = number;
    this.iVal = iVal;
  }

  public boolean equals(OSCEvent event) {
    return event.type == this.type && event.number == this.number;
  }
}
