package btc_strat.Model.Inputs;

import java.util.List;

import btc_strat.Model.Candlestick;

public class Input {
  List<Candlestick> candleList;
  List<Candlestick> candleList1h;
  float profitToBeat;
  int poisonPill;
  int start;

  public Input() {
  }

  public List<Candlestick> getCandleList() {
    return this.candleList;
  }

  public void setCandleList(List<Candlestick> candleList) {
    this.candleList = candleList;
  }

  public List<Candlestick> getCandleList1h() {
    return this.candleList1h;
  }

  public void setCandleList1h(List<Candlestick> candleList1h) {
    this.candleList1h = candleList1h;
  }

  public float getProfitToBeat() {
    return this.profitToBeat;
  }

  public void setProfitToBeat(float profitToBeat) {
    this.profitToBeat = profitToBeat;
  }

  public int getPoisonPill() {
    return this.poisonPill;
  }

  public void setPoisonPill(int poisonPill) {
    this.poisonPill = poisonPill;
  }

  public int getStart() {
    return this.start;
  }

  public void setStart(int start) {
    this.start = start;
  }

}