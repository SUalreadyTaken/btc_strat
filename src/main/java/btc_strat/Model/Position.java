package btc_strat.Model;

public class Position {
  boolean position;
  float open;

  public Position(boolean position, float open) {
    this.position = position;
    this.open = open;
  }

  public boolean isPosition() {
    return this.position;
  }

  public boolean getPosition() {
    return this.position;
  }

  public void setPosition(boolean position) {
    this.position = position;
  }

  public float getOpen() {
    return this.open;
  }

  public void setOpen(float open) {
    this.open = open;
  }

}