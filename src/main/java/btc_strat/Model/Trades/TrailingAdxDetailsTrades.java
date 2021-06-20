package btc_strat.Model.Trades;

import java.util.List;

import btc_strat.Model.Details;
import btc_strat.Model.NegativeDetail;

public class TrailingAdxDetailsTrades extends TrailingAdxTrades {
  List<Details> detailsList;
  List<NegativeDetail> negativeDetails;

  public TrailingAdxDetailsTrades() {
  }

  public List<Details> getDetailsList() {
    return this.detailsList;
  }

  public void setDetailsList(List<Details> detailsList) {
    this.detailsList = detailsList;
  }

  public List<NegativeDetail> getNegativeDetails() {
    return this.negativeDetails;
  }

  public void setNegativeDetails(List<NegativeDetail> negativeDetails) {
    this.negativeDetails = negativeDetails;
  }

}
