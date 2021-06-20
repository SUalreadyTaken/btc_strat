package btc_strat.Model.Trades;

import java.util.List;

public class Trades {
  float profit;
  List<Float> tradePercentages;
  List<Float> profitList;
  List<Float> longPercentages;
  List<Float> shortPercentages;
  float conf;

  public Trades() {
  }

  public List<Float> getProfitList() {
    return this.profitList;
  }

  public void setProfitList(List<Float> profitList) {
    this.profitList = profitList;
  }

  public float getConf() {
    return this.conf;
  }

  public void setConf(float conf) {
    this.conf = conf;
  }

  public float getProfit() {
    return this.profit;
  }

  public void setProfit(float profit) {
    this.profit = profit;
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