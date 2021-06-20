package btc_strat.Model.Trades;

import java.util.List;

import btc_strat.Model.Details;

public class AdxFractalDetailsTrades extends AdxTrades {
  List<Details> detailsList;

  public AdxFractalDetailsTrades() {
  }

  public List<Details> getDetailsList() {
    return this.detailsList;
  }

  public void setDetailsList(List<Details> detailsList) {
    this.detailsList = detailsList;
  }

}
