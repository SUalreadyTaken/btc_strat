package btc_strat.Model;

import java.util.List;

public class MRBands {
  List<Float> upperValues;
  List<Float> lowerValues;


  public MRBands(List<Float> upperValues, List<Float> lowerValues) {
    this.upperValues = upperValues;
    this.lowerValues = lowerValues;
  }

  public List<Float> getUpperValues() {
    return this.upperValues;
  }

  public void setUpperValues(List<Float> upperValues) {
    this.upperValues = upperValues;
  }

  public List<Float> getLowerValues() {
    return this.lowerValues;
  }

  public void setLowerValues(List<Float> lowerValues) {
    this.lowerValues = lowerValues;
  }

}
