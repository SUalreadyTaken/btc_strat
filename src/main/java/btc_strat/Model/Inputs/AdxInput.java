package btc_strat.Model.Inputs;

import java.util.List;

import btc_strat.Model.DI;

public class AdxInput extends Input {
  int lookback;
  int longThresh;
  int exitThresh;
  List<Float> adxList;
  int adx;
  DI diList;

  public AdxInput() {
  }

  public int getLookback() {
    return this.lookback;
  }

  public void setLookback(int lookback) {
    this.lookback = lookback;
  }

  public int getLongThresh() {
    return this.longThresh;
  }

  public void setLongThresh(int longThresh) {
    this.longThresh = longThresh;
  }

  public int getExitThresh() {
    return this.exitThresh;
  }

  public void setExitThresh(int exitThresh) {
    this.exitThresh = exitThresh;
  }

  public List<Float> getAdxList() {
    return this.adxList;
  }

  public void setAdxList(List<Float> adxList) {
    this.adxList = adxList;
  }

  public int getAdx() {
    return this.adx;
  }

  public void setAdx(int adx) {
    this.adx = adx;
  }

  public DI getDiList() {
    return this.diList;
  }

  public void setDiList(DI diList) {
    this.diList = diList;
  }
}
