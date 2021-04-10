package btc_strat.Model;

import java.util.List;

public class RawData {
  List<List<Float>> adxList;
  List<DI> diList;
  List<Candlestick> candleList;
  List<Candlestick> candleList1h;
  List<Kijun> kijunList;
  List<List<List<Float>>> atrTrailingMultipliesList;
  List<Float> wList;


  public List<Float> getWList() {
    return this.wList;
  }

  public void setWList(List<Float> wList) {
    this.wList = wList;
  }


  public RawData() {}

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

  public List<List<Float>> getAdxList() {
    return this.adxList;
  }

  public void setAdxList(List<List<Float>> adxList) {
    this.adxList = adxList;
  }

  public List<DI> getDiList() {
    return this.diList;
  }

  public void setDiList(List<DI> diList) {
    this.diList = diList;
  }

  public List<Candlestick> getCandleList() {
    return this.candleList;
  }

  public void setCandleList(List<Candlestick> candleList) {
    this.candleList = candleList;
  }

  public List<Kijun> getKijunList() {
    return this.kijunList;
  }

  public void setKijunList(List<Kijun> kijunList) {
    this.kijunList = kijunList;
  }
  
}