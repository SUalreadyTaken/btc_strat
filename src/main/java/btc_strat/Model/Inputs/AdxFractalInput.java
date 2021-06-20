package btc_strat.Model.Inputs;

import btc_strat.Model.FractalsDefault;

public class AdxFractalInput extends AdxInput {
  FractalsDefault fractalsDefault;

  public AdxFractalInput() {
  }

  public FractalsDefault getFractalsDefault() {
    return this.fractalsDefault;
  }

  public void setFractalsDefault(FractalsDefault fractalsDefault) {
    this.fractalsDefault = fractalsDefault;
  }
}