package btc_strat.Model.Inputs;

import java.util.List;

import btc_strat.Model.Kijun;

public class KijunAdxInput extends AdxInput {
  List<Kijun> wholeKijunList;

  public KijunAdxInput() {
  }

  public List<Kijun> getWholeKijunList() {
    return this.wholeKijunList;
  }

  public void setWholeKijunList(List<Kijun> wholeKijunList) {
    this.wholeKijunList = wholeKijunList;
  }
}