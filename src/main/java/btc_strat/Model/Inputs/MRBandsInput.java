package btc_strat.Model.Inputs;

public class MRBandsInput extends Input {
  int window;
  int degree;
  float multiplier;

  public MRBandsInput() {
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
