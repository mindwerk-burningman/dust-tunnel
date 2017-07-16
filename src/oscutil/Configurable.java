package oscutil;

/**
 * Simple wrapper used to pass primitive variables by reference rather than value
 */
public class Configurable {
  public float fVal;
  public int iVal;
  public String sVal;
  public boolean bVal;

  public Configurable(float value) {
    this.fVal = value;
  }

  public Configurable(int value) {
    this.iVal = value;
  }

  public Configurable(String value) {
    this.sVal = value;
  }

  public Configurable(boolean value) {
    this.bVal = value;
  }
}
