package btc_strat.Model.Trades;

public class AdxTrades extends Trades {
  int adx;
  int lookback;
  int longThresh;
  int exitThresh;

  public AdxTrades() {
  }

  public int getAdx() {
    return this.adx;
  }

  public void setAdx(int adx) {
    this.adx = adx;
  }

  public int getLookback() {
    return this.lookback;
  }

  public void setLookback(int lookback) {
    this.lookback = lookback;
  }

  public int getLongThresh() {
    return this.longThresh;
  }

  public void setLongThresh(int longThresh) {
    this.longThresh = longThresh;
  }

  public int getExitThresh() {
    return this.exitThresh;
  }

  public void setExitThresh(int exitThresh) {
    this.exitThresh = exitThresh;
  }

}
