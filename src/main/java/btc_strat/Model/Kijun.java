package btc_strat.Model;

import java.util.List;

public class Kijun {
  List<Float> kijun;
  List<Boolean> isOverCloseList;

  public Kijun(List<Float> kijun, List<Boolean> isOverCloseList) {
    this.kijun = kijun;
    this.isOverCloseList = isOverCloseList;
  }

  public List<Float> getKijun() {
    return this.kijun;
  }

  public void setKijun(List<Float> kijun) {
    this.kijun = kijun;
  }

  public List<Boolean> getIsOverCloseList() {
    return this.isOverCloseList;
  }

  public void setIsOverCloseList(List<Boolean> isOverCloseList) {
    this.isOverCloseList = isOverCloseList;
  }
  
}