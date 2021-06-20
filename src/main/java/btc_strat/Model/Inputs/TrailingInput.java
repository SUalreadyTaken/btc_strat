package btc_strat.Model.Inputs;

import java.util.List;

public class TrailingInput extends Input {
  int atr;
  List<List<Boolean>> multiplierIsLongList;

  public TrailingInput() {
  }

  public int getAtr() {
    return this.atr;
  }

  public void setAtr(int atr) {
    this.atr = atr;
  }

  public List<List<Boolean>> getMultiplierIsLongList() {
    return this.multiplierIsLongList;
  }

  public void setMultiplierIsLongList(List<List<Boolean>> getMultiplierIsLongList) {
    this.multiplierIsLongList = getMultiplierIsLongList;
  }

}
