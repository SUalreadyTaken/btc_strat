package btc_strat.Model.Trades;

public class MRBandsTrades extends Trades {
  int window;
  int degree;
  float multiplier;

  public MRBandsTrades() {
  }

  public int getWindow() {
    return this.window;
  }

  public void setWindow(int window) {
    this.window = window;
  }

  public int getDegree() {
    return this.degree;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  public float getMultiplier() {
    return this.multiplier;
  }

  public void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
  }

}
