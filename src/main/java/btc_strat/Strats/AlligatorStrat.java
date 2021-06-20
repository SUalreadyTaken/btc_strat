package btc_strat.Strats;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.FractalsDefault;
import btc_strat.Model.Trades.AlligatorFractalTrades;

public class AlligatorStrat {
  StratUtils stratUtils = new StratUtils();

  public AlligatorStrat() {
  }

  public List<Float> alligatorFractalProfitList(List<Float> lips, List<Float> teeth, List<Float> jaws,
      FractalsDefault fractals, List<Candlestick> candleList, int start) {

    float profit = 1;
    float open = 0f;
    List<Float> profitList = new ArrayList<>();
    boolean position = false;
    float stopLoss = fractals.getDownFractalList().get(start);
    float tmpLoss = fractals.getDownFractalList().get(start);

    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        // looking for long
        if (lips.get(i) > jaws.get(i) && lips.get(i) > teeth.get(i) && teeth.get(i) > jaws.get(i)
            && candleList.get(i).getClose() > fractals.getTopFractalList().get(i)) {
          position = !position;
          open = candleList.get(i).getClose();
        }
      } else {
        // looking to exit or stop hit
        if (candleList.get(i).getClose() < stopLoss) {
          // stop hit
          position = !position;
          profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
          profitList.add(profit);
          continue;
        }
        if (lips.get(i) < jaws.get(i) && lips.get(i) < teeth.get(i) && teeth.get(i) < jaws.get(i)
            && candleList.get(i).getClose() < fractals.getDownFractalList().get(i)) {
          // take profit
          position = !position;
          profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
          profitList.add(profit);
          continue;
        }
      }
      if (fractals.getDownFractalList().get(i) != tmpLoss) {
        // stopLoss equals previous fractal
        stopLoss = tmpLoss;
        tmpLoss = fractals.getDownFractalList().get(i);
      }
    }

    return profitList;
  }

  public AlligatorFractalTrades alligatorFractalPrint(List<Float> lips, List<Float> teeth, List<Float> jaws,
      FractalsDefault fractals, List<Candlestick> candleList, int start) {

    float profit = 1;
    float open = 0f;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    boolean position = false;
    float stopLoss = fractals.getDownFractalList().get(start);
    float tmpLoss = fractals.getDownFractalList().get(start);
    int stopsHit = 0;

    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        // looking for long
        if (lips.get(i) > jaws.get(i) && lips.get(i) > teeth.get(i) && teeth.get(i) > jaws.get(i)
            && candleList.get(i).getClose() > fractals.getTopFractalList().get(i)) {
          position = !position;
          open = candleList.get(i).getClose();
          tmpLoss = fractals.getDownFractalList().get(i);
          for (int j = i; j > 0; j--) {
            if (tmpLoss != fractals.getDownFractalList().get(j)) {
              stopLoss = fractals.getDownFractalList().get(j);
              break;
            }
          }
        }
      } else {
        // looking to exit or stop hit
        // if (candleList.get(i).getClose() < stopLoss) {
        // // stop hit
        // position = !position;
        // stopsHit++;
        // profit = closeLongTrade(profit, open, candleList.get(i).getClose(),
        // tradePercentages, longPercentages);
        // profitList.add(profit);
        // } else
        if (lips.get(i) < jaws.get(i) && lips.get(i) < teeth.get(i) && teeth.get(i) < jaws.get(i)
            && candleList.get(i).getClose() < fractals.getDownFractalList().get(i)) {
          // take profit
          position = !position;
          profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages,
              longPercentages);
          profitList.add(profit);
        }
        // if(fractals.getDownFractalList().get(i) != tmpLoss &&
        // fractals.getDownFractalList().get(i) > tmpLoss) {
        // stopLoss = tmpLoss;
        // tmpLoss = fractals.getDownFractalList().get(i);
        // }

      }
    }

    if (position) {
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(),
          tradePercentages, longPercentages);
      profitList.add(profit);
    }

    AlligatorFractalTrades result = new AlligatorFractalTrades();
    // float conf = calcConf(profitList);
    result.setStopsHit(stopsHit);
    result.setLongPercentages(longPercentages);
    result.setProfit(profit);
    // result.setConf(conf);
    result.setTradePercentages(tradePercentages);
    result.setProfitList(profitList);
    return result;
  }

  public AlligatorFractalTrades alligatorFractalTrades(List<Float> lips, List<Float> teeth, List<Float> jaws,
      FractalsDefault fractals, List<Candlestick> candleList, int start) {

    float profit = 1;
    float open = 0f;
    List<Float> profitList = new ArrayList<>();
    // List<Float> tradePercentages = new ArrayList<Float>();
    // List<Float> longPercentages = new ArrayList<Float>();
    boolean position = false;
    float stopLoss = fractals.getDownFractalList().get(start);
    float tmpLoss = fractals.getDownFractalList().get(start);

    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        // looking for long
        if (lips.get(i) > jaws.get(i) && lips.get(i) > teeth.get(i) && teeth.get(i) > jaws.get(i)
            && candleList.get(i).getClose() > fractals.getTopFractalList().get(i)) {
          position = !position;
          open = candleList.get(i).getClose();
          tmpLoss = fractals.getDownFractalList().get(i);
          for (int j = i; j > 0; j--) {
            if (tmpLoss != fractals.getDownFractalList().get(j)) {
              stopLoss = fractals.getDownFractalList().get(j);
              break;
            }
          }
        }
      } else {
        // looking to exit or stop hit
        // if (candleList.get(i).getClose() < stopLoss) {
        // // stop hit
        // position = !position;
        // profit = closeLongProfit(profit, open, candleList.get(i).getClose());
        // profitList.add(profit);
        // tmpLoss = 0f;
        // continue;
        // } else
        if (lips.get(i) < jaws.get(i) && lips.get(i) < teeth.get(i) && teeth.get(i) < jaws.get(i)
            && candleList.get(i).getClose() < fractals.getDownFractalList().get(i)) {
          // take profit
          position = !position;
          profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
          profitList.add(profit);
          tmpLoss = 0f;
          continue;
        }
        // if(fractals.getDownFractalList().get(i) != tmpLoss &&
        // fractals.getDownFractalList().get(i) > tmpLoss) {
        // stopLoss = tmpLoss;
        // tmpLoss = fractals.getDownFractalList().get(i);
        // }
      }
    }

    if (position) {
      profit = stratUtils.closeLongProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    }

    AlligatorFractalTrades result = new AlligatorFractalTrades();
    // float conf = calcConf(profitList);
    // result.setLongPercentages(longPercentages);
    result.setProfit(profit);

    // result.setConf(conf);
    // result.setTradePercentages(tradePercentages);
    result.setProfitList(profitList);
    return result;
  }

}
