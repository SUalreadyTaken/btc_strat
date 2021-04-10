package btc_strat.Model;

import java.util.List;

public class InputModel {
  int adx = 0;
  List<Float> adxList;
  DI diList;
  List<Kijun> wholeKijunList;
  List<List<List<Float>>> atrTrailingMultipliesList;
  List<Candlestick> candleList;
  List<Candlestick> candleList1h;
  int atr;
  List<List<Boolean>> multiplierIsLongList;
  int canalLookback = 1;
  int adxLookback = 1;
  List<Canal> canalList;
  int poisonPill = 0;
  int window;
  int degree;
  float mult;
  List<Float> wList;

  public List<Float> getWList() {
    return this.wList;
  }

  public void setWList(List<Float> wList) {
    this.wList = wList;
  }


  public InputModel() {
  }


  public int getWindow() {
    return this.window;
  }

  public void setWindow(int window) {
    this.window = window;
  }

  public int getDegree() {
    return this.degree;
  }

  public void setDegree(int degree) {
    this.degree = degree;
  }

  public float getMult() {
    return this.mult;
  }

  public void setMult(float mult) {
    this.mult = mult;
  }



  public List<Canal> getCanalList() {
    return this.canalList;
  }

  public void setCanalList(List<Canal> canalList) {
    this.canalList = canalList;
  }


  public int getCanalLookback() {
    return this.canalLookback;
  }

  public void setCanalLookback(int canalLookback) {
    this.canalLookback = canalLookback;
  }

  public int getAdxLookback() {
    return this.adxLookback;
  }

  public void setAdxLookback(int adxLookback) {
    this.adxLookback = adxLookback;
  }

  public List<List<List<Float>>> getAtrTrailingMultipliesList() {
    return this.atrTrailingMultipliesList;
  }

  public void setAtrTrailingMultipliesList(List<List<List<Float>>> atrTrailingMultipliesList) {
    this.atrTrailingMultipliesList = atrTrailingMultipliesList;
  }

  public List<Candlestick> getCandleList1h() {
    return this.candleList1h;
  }

  public void setCandleList1h(List<Candlestick> candleList1h) {
    this.candleList1h = candleList1h;
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

  public void setMultiplierIsLongList(List<List<Boolean>> multiplierIsLongList) {
    this.multiplierIsLongList = multiplierIsLongList;
  }

  public int getAdx() {
    return this.adx;
  }

  public void setAdx(int adx) {
    this.adx = adx;
  }

  public List<Float> getAdxList() {
    return this.adxList;
  }

  public void setAdxList(List<Float> adxList) {
    this.adxList = adxList;
  }

  public DI getDiList() {
    return this.diList;
  }

  public void setDiList(DI diList) {
    this.diList = diList;
  }

  public List<Kijun> getWholeKijunList() {
    return this.wholeKijunList;
  }

  public void setWholeKijunList(List<Kijun> wholeKijunList) {
    this.wholeKijunList = wholeKijunList;
  }

  public List<Candlestick> getCandleList() {
    return this.candleList;
  }

  public void setCandleList(List<Candlestick> candleList) {
    this.candleList = candleList;
  }

  public int getPoisonPill() {
    return this.poisonPill;
  }

  public void setPoisonPill(int poisonPill) {
    this.poisonPill = poisonPill;
  }

}