package btc_strat.Strats;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.Trades.TrailingTrades;

public class TrailingStrat {
  StratUtils stratUtils = new StratUtils();

  public TrailingStrat() {
  }

  public TrailingTrades getTrailingTrades(int atrCount, List<Boolean> isLongList, List<Candlestick> candleList) {
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
            profit = stratUtils.closeLongExtra(profit, candleList.get(i).getClose(), tmpOpen, tradePercentages,
                longPercentages);
          }
          tmpOpen = candleList.get(i).getClose();
          position = !position;
        }
      } else {
        // am short look out for switch to long
        if (isLongList.get(i)) {
          if (tmpOpen != 0) {
            profit = stratUtils.closeShortExtra(profit, candleList.get(i).getClose(), tmpOpen, tradePercentages,
                shortPercentages);
          }
          tmpOpen = candleList.get(i).getClose();
          position = !position;
        }
      }
    }
    if (position) {
      profit = stratUtils.closeLongExtra(profit, candleList.get(candleList.size() - 1).getClose(), tmpOpen,
          tradePercentages, longPercentages);
    } else {
      profit = stratUtils.closeShortExtra(profit, candleList.get(candleList.size() - 1).getClose(), tmpOpen,
          tradePercentages, shortPercentages);
    }
    TrailingTrades res = new TrailingTrades();
    res.setTradePercentages(tradePercentages);
    res.setShortPercentages(shortPercentages);
    res.setLongPercentages(longPercentages);
    res.setProfit(profit);
    return res;
  }

}