package btc_strat.Model;

import java.util.List;

public class Trades {

  float profit;
  int kijun;
  int adx;
  int adxThreshold;
  int adxMinThreshold;
  int tradeCount;
  float lowest;
  float atrMultiplier;
  int atr;
  List<Details> detailsList;
  List<Float> tradePercentages;
  List<Float> longPercentages;
  List<Float> shortPercentages;
  List<NegativeDetail> top5Negative;
  int closeLongThreshold;
  int closeShortThreshold;
  float conf;
  int adxLookback;
  int canalLookback;
  Canal canal;
  int window;
  int degree;
  float mult;

  public Trades() {
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



  public Canal getCanal() {
    return this.canal;
  }

  public void setCanal(Canal canal) {
    this.canal = canal;
  }



  public int getAdxLookback() {
    return this.adxLookback;
  }

  public void setAdxLookback(int adxLookback) {
    this.adxLookback = adxLookback;
  }

  public int getCanalLookback() {
    return this.canalLookback;
  }

  public void setCanalLookback(int canalLookback) {
    this.canalLookback = canalLookback;
  }


  public float getConf() {
    return this.conf;
  }

  public void setConf(float conf) {
    this.conf = conf;
  }

  public int getCloseLongThreshold() {
    return this.closeLongThreshold;
  }

  public void setCloseLongThreshold(int closeLongThreshold) {
    this.closeLongThreshold = closeLongThreshold;
  }

  public int getCloseShortThreshold() {
    return this.closeShortThreshold;
  }

  public void setCloseShortThreshold(int closeShortThreshold) {
    this.closeShortThreshold = closeShortThreshold;
  }
  



  public List<NegativeDetail> getTop5Negative() {
    return this.top5Negative;
  }

  public void setTop5Negative(List<NegativeDetail> top5Negative) {
    this.top5Negative = top5Negative;
  }


  public List<Details> getDetailsList() {
    return this.detailsList;
  }

  public void setDetailsList(List<Details> detailsList) {
    this.detailsList = detailsList;
  }

  public float getAtrMultiplier() {
    return this.atrMultiplier;
  }

  public void setAtrMultiplier(float atrMultiplier) {
    this.atrMultiplier = atrMultiplier;
  }

  public int getAtr() {
    return this.atr;
  }

  public void setAtr(int atr) {
    this.atr = atr;
  }
 
  public int getTradeCount() {
    return this.tradeCount;
  }

  public void setTradeCount(int tradeCount) {
    this.tradeCount = tradeCount;
  }

  public float getLowest() {
    return this.lowest;
  }

  public void setLowest(float lowest) {
    this.lowest = lowest;
  }

  public float getProfit() {
    return this.profit;
  }

  public void setProfit(float profit) {
    this.profit = profit;
  }

  public int getKijun() {
    return this.kijun;
  }

  public void setKijun(int kijun) {
    this.kijun = kijun;
  }

  public int getAdx() {
    return this.adx;
  }

  public void setAdx(int adx) {
    this.adx = adx;
  }

  public int getAdxThreshold() {
    return this.adxThreshold;
  }

  public void setAdxThreshold(int adxThreshold) {
    this.adxThreshold = adxThreshold;
  }

  public int getAdxMinThreshold() {
    return this.adxMinThreshold;
  }

  public void setAdxMinThreshold(int adxMinThreshold) {
    this.adxMinThreshold = adxMinThreshold;
  }

  public List<Float> getTradePercentages() {
    return this.tradePercentages;
  }

  public void setTradePercentages(List<Float> tradePercentages) {
    this.tradePercentages = tradePercentages;
  }

  public List<Float> getLongPercentages() {
    return this.longPercentages;
  }

  public void setLongPercentages(List<Float> longPercentages) {
    this.longPercentages = longPercentages;
  }

  public List<Float> getShortPercentages() {
    return this.shortPercentages;
  }

  public void setShortPercentages(List<Float> shortPercentages) {
    this.shortPercentages = shortPercentages;
  }
  
}