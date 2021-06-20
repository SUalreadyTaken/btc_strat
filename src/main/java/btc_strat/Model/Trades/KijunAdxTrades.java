package btc_strat.Model.Trades;

public class KijunAdxTrades extends AdxTrades {
  int kijunLen;
  float lowest;

  public KijunAdxTrades() {
  }

  public int getKijunLen() {
    return this.kijunLen;
  }

  public void setKijunLen(int kijunLen) {
    this.kijunLen = kijunLen;
  }

  public float getLowest() {
    return this.lowest;
  }

  public void setLowest(float lowest) {
    this.lowest = lowest;
  }

}
