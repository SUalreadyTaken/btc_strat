package btc_strat.Strats;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.MRBands;
import btc_strat.Model.Trades;

public class MRBandsStrat {

  StratUtils stratUtils = new StratUtils();

  public MRBandsStrat(){}
  
  public List<Float> mrbLongOnlyProfitList(MRBands mrb, int window, List<Candlestick> candleList) {
    int start = (int) (window * 1.5);
    float profit = 1.0f;
    boolean position = true;
    float tmpOpen = 0.0f;
    for (int i = start; i < candleList.size() - 2; i++) {
      if (candleList.get(i).getClose() > mrb.getUpperValues().get(i)) {
        start = i;
        tmpOpen = candleList.get(i).getClose();
        break;
      } else if (candleList.get(i).getClose() < mrb.getLowerValues().get(i)){
        start = i;
        tmpOpen = candleList.get(i).getClose();
        position = false;
        break;
      }
    }
    List<Float> profitList = new ArrayList<>();
    for (int i = start; i < candleList.size() - 2; i++) {
      if (position) {
        if (candleList.get(i).getClose() < mrb.getLowerValues().get(i)) {
          // close long go short;
          profit = stratUtils.closeLongProfit(profit, tmpOpen, candleList.get(i).getClose());
          profitList.add(profit);
          tmpOpen = candleList.get(i).getClose();
          position = false;
        }
      } else {
        if (candleList.get(i).getClose() > mrb.getUpperValues().get(i)) {
          profit = stratUtils.closeShortProfit(profit, tmpOpen, candleList.get(i).getClose());
          profitList.add(profit);
          tmpOpen = candleList.get(i).getClose();
          position = true;
        }
      }
    }

    if (position) {
      profit = stratUtils.closeLongProfit(profit, tmpOpen, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    } else {
      profit = stratUtils.closeShortProfit(profit, tmpOpen, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    }
    return profitList;
  }

  public Trades mrbLongOnlyTrade(MRBands mrb, int window, List<Candlestick> candleList) {
    int start = (int) (window * 1.5);
    float profit = 1.0f;
    boolean position = true;
    float tmpOpen = 0.0f;
    for (int i = start; i < candleList.size() - 2; i++) {
      if (candleList.get(i).getClose() > mrb.getUpperValues().get(i)) {
        start = i;
        tmpOpen = candleList.get(i).getClose();
        break;
      } else if (candleList.get(i).getClose() < mrb.getLowerValues().get(i)){
        start = i;
        tmpOpen = candleList.get(i).getClose();
        position = false;
        break;
      }
    }
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    for (int i = start; i < candleList.size() - 2; i++) {
      if (position) {
        if (candleList.get(i).getClose() < mrb.getLowerValues().get(i)) {
          // close long go short;
          profit = stratUtils.closeLongTrade(profit, tmpOpen, candleList.get(i).getClose(), tradePercentages, longPercentages);
          profitList.add(profit);
          tmpOpen = candleList.get(i).getClose();
          position = false;
        }
      } else {
        if (candleList.get(i).getClose() > mrb.getUpperValues().get(i)) {
          profit = stratUtils.closeShortTrade(profit, tmpOpen, candleList.get(i).getClose(), tradePercentages, shortPercentages);
          profitList.add(profit);
          tmpOpen = candleList.get(i).getClose();
          position = true;
        }
      }
    }

    if (position) {
      profit = stratUtils.closeLongTrade(profit, tmpOpen, candleList.get(candleList.size() - 1).getClose(), tradePercentages, longPercentages);
      profitList.add(profit);
    } else {
      profit = stratUtils.closeShortTrade(profit, tmpOpen, candleList.get(candleList.size() - 1).getClose(), tradePercentages, shortPercentages);
      profitList.add(profit);
    }
    Trades result = new Trades();
    result.setWindow(window);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setConf(stratUtils.calcConf(profitList));
    return result;
  }

}
