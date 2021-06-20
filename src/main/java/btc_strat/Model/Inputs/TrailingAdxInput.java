package btc_strat.Model.Inputs;

import java.util.List;

public class TrailingAdxInput extends KijunAdxInput {
  List<List<List<Float>>> atrTrailingMultipliesList;

  public TrailingAdxInput() {
  }

  public List<List<List<Float>>> getAtrTrailingMultipliesList() {
    return this.atrTrailingMultipliesList;
  }

  public void setAtrTrailingMultipliesList(List<List<List<Float>>> atrTrailingMultipliesList) {
    this.atrTrailingMultipliesList = atrTrailingMultipliesList;
  }

}
