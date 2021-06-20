package btc_strat.Model.Trades;

import java.util.List;

import btc_strat.Model.Details;

public class CanalAdxDetailsTrades extends CanalAdxTrades {
  List<Details> detailsList;
  float lowest;

  public CanalAdxDetailsTrades() {
  }

  public List<Details> getDetailsList() {
    return this.detailsList;
  }

  public void setDetailsList(List<Details> detailsList) {
    this.detailsList = detailsList;
  }

  public float getLowest() {
    return this.lowest;
  }

  public void setLowest(float lowest) {
    this.lowest = lowest;
  }
}
