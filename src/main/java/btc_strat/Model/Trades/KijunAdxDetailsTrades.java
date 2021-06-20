package btc_strat.Model.Trades;

import java.util.List;

import btc_strat.Model.Details;

public class KijunAdxDetailsTrades extends KijunAdxTrades {
  List<Details> detailsList;

  public KijunAdxDetailsTrades() {
  }

  public List<Details> getDetailsList() {
    return this.detailsList;
  }

  public void setDetailsList(List<Details> detailsList) {
    this.detailsList = detailsList;
  }
}
