package btc_strat.Model;

import java.util.List;

public class FractalsDefault {
  List<Float> topFractalList;
  List<Float> downFractalList;

  public FractalsDefault(List<Float> topFractalList, List<Float> downfractalList) {
    this.topFractalList = topFractalList;
    this.downFractalList = downfractalList;
  }

  public List<Float> getTopFractalList() {
    return this.topFractalList;
  }

  public void setTopFractalList(List<Float> topFractalList) {
    this.topFractalList = topFractalList;
  }

  public List<Float> getDownFractalList() {
    return this.downFractalList;
  }

  public void setDownFractalList(List<Float> downfractalList) {
    this.downFractalList = downfractalList;
  }

}
