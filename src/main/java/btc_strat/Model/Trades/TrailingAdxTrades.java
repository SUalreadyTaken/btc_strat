package btc_strat.Model.Trades;

public class TrailingAdxTrades extends KijunAdxTrades {
  int atr;
  float atrMultiplier;
  float lowest;

  public TrailingAdxTrades() {
  }

  public int getAtr() {
    return this.atr;
  }

  public void setAtr(int atr) {
    this.atr = atr;
  }

  public float getAtrMultiplier() {
    return this.atrMultiplier;
  }

  public void setAtrMultiplier(float atrMultiplier) {
    this.atrMultiplier = atrMultiplier;
  }

  public float getLowest() {
    return this.lowest;
  }

  public void setLowest(float lowest) {
    this.lowest = lowest;
  }

}
