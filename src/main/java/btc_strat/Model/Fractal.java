package btc_strat.Model;

public class Fractal {
  private boolean isLow = false;
  private float stopLoss = 0.0f;

  public Fractal(boolean isLow, float stopLoss) {
    this.isLow = isLow;
    this.stopLoss = stopLoss;
  }

  public boolean getIsLow() {
    return this.isLow;
  }

  public void setIsLow(boolean isLow) {
    this.isLow = isLow;
  }

  public float getStopLoss() {
    return this.stopLoss;
  }

  public void setStopLoss(float stopLoss) {
    this.stopLoss = stopLoss;
  }

}
