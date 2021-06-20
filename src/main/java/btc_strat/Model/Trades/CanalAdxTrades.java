package btc_strat.Model.Trades;

import btc_strat.Model.Canal;

public class CanalAdxTrades extends AdxTrades {
  Canal canal;
  int canalLookback;

  public CanalAdxTrades() {
  }

  public Canal getCanal() {
    return this.canal;
  }

  public void setCanal(Canal canal) {
    this.canal = canal;
  }

  public int getCanalLookback() {
    return this.canalLookback;
  }

  public void setCanalLookback(int canalLookback) {
    this.canalLookback = canalLookback;
  }

}
