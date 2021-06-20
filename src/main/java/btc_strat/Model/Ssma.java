package btc_strat.Model;

import java.util.List;

public class Ssma {
  int length;
  List<List<Float>> values;

  public Ssma(int length, List<List<Float>> values) {
    this.length = length;
    this.values = values;
  }

  public int getLength() {
    return this.length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<List<Float>> getValues() {
    return this.values;
  }

  public void setValues(List<List<Float>> values) {
    this.values = values;
  }

}
