package btc_strat.Strats;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.Trades;

public class TrailingStrat {
  public TrailingStrat() {
  }

  public Trades getTrailingTrades(int atrCount, List<Boolean> isLongList, List<Candlestick> candleList) {
    int start = atrCount * 2;
    boolean position = isLongList.get(start);
    float tmpOpen = 0;
    float profit = 1;
    List<Float> tradePercentages = new ArrayList<>();
    List<Float> longPercentages = new ArrayList<>();
    List<Float> shortPercentages = new ArrayList<>();
    for (int i = start + 1; i < candleList.size(); i++) {
      if (position) {
        // am long look out for switch to short
        if (!isLongList.get(i)) {
          if (tmpOpen != 0) {
            profit = closeLongExtra(profit, candleList.get(i).getClose(), tmpOpen, tradePercentages, longPercentages);
          }
          tmpOpen = candleList.get(i).getClose();
          position = !position;
        }
      } else {
        // am short look out for switch to long
        if (isLongList.get(i)) {
          if (tmpOpen != 0) {
            profit = closeShortExtra(profit, candleList.get(i).getClose(), tmpOpen, tradePercentages, shortPercentages);
          }
          tmpOpen = candleList.get(i).getClose();
          position = !position;
        }
      }
    }
    if (position) {
      profit = closeLongExtra(profit, candleList.get(candleList.size() -1).getClose(), tmpOpen, tradePercentages, longPercentages);
    } else {
      profit = closeShortExtra(profit, candleList.get(candleList.size() -1).getClose(), tmpOpen, tradePercentages, shortPercentages);
    }
    Trades res = new Trades();
    res.setTradePercentages(tradePercentages);
    res.setShortPercentages(shortPercentages);
    res.setLongPercentages(longPercentages);
    res.setProfit(profit);
    return res;
  }

  private float closeLongExtra(float profit, float close, float open, List<Float> tradePercentages,
      List<Float> longPercentages) {

    float percentage = ((close - open) / open);
    tradePercentages.add(percentage * 100);
    longPercentages.add(percentage * 100);
    return (float) (1 + (percentage - 0.00075)) * profit;
  }

  private float closeShortExtra(float profit, float close, float open, List<Float> tradePercentages,
      List<Float> shortPercentages) {

    float percentage = ((close - open) / open);
    tradePercentages.add(-(percentage * 100));
    shortPercentages.add(-(percentage * 100));
    return (float) (1 - (percentage + 0.00075)) * profit;
  }
}