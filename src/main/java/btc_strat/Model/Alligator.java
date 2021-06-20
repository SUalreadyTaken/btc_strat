package btc_strat.Model;

public class Alligator {
  private float jaw;
  private float teeth;
  private float lips;

  public Alligator(float jaw, float teeth, float lips) {
    this.jaw = jaw;
    this.teeth = teeth;
    this.lips = lips;
  }

  public float getJaw() {
    return this.jaw;
  }

  public void setJaw(float jaw) {
    this.jaw = jaw;
  }

  public float getTeeth() {
    return this.teeth;
  }

  public void setTeeth(float teeth) {
    this.teeth = teeth;
  }

  public float getLips() {
    return this.lips;
  }

  public void setLips(float lips) {
    this.lips = lips;
  }

}
