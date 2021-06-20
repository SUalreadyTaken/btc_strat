package btc_strat.Strats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.DI;
import btc_strat.Model.Details;
import btc_strat.Model.FractalsDefault;
import btc_strat.Model.PositionDetails;
import btc_strat.Model.Trades.AdxFractalDetailsTrades;
import btc_strat.Model.Trades.AdxTrades;

public class AdxFractalStrat {

  StratUtils stratUtils = new StratUtils();

  public AdxFractalStrat() {
  }

  public List<Float> adxFractalProfitList(int start, int adxLookback, int longThresh, int exitThresh,
      FractalsDefault fractals, List<Float> adxList, DI di, List<Candlestick> candlelist) {
    float profit = 1;
    List<Float> profitList = new ArrayList<>();
    boolean position = false;
    float open = 0.0f;

    for (int i = start; i < candlelist.size(); i++) {
      if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
          && fractals.getTopFractalList().get(i) < candlelist.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // go long
        position = !position;
        open = candlelist.get(i).getClose();
        start = i;
        break;
      } else if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
          && fractals.getDownFractalList().get(i) > candlelist.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // starts with no position .. all ok
        start = i;
        break;
      }
    }

    for (int i = start; i < candlelist.size(); i++) {
      if (!position) {
        if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
            && fractals.getTopFractalList().get(i) < candlelist.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          // go long
          position = !position;
          open = candlelist.get(i).getClose();
        }

      } else {
        if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
            && fractals.getDownFractalList().get(i) > candlelist.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          profit = stratUtils.closeLongProfit(profit, open, candlelist.get(i).getClose());
          profitList.add(profit);
          position = !position;
        }
      }
    }

    if (position) {
      profit = stratUtils.closeLongProfit(profit, open, candlelist.get(candlelist.size() - 1).getClose());
      profitList.add(profit);
    }

    return profitList;
  }

  public AdxTrades adxFractalTrades(AdxTrades t, int start, int adxLookback, int longThresh, int exitThresh,
      FractalsDefault fractals, List<Float> adxList, DI di, List<Candlestick> candleList) {
    float profit = 1;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    boolean position = false;
    float open = 0.0f;

    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
          && fractals.getTopFractalList().get(i) < candleList.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // go long
        position = !position;
        open = candleList.get(i).getClose();
        start = i;
        break;
      } else if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
          && fractals.getDownFractalList().get(i) > candleList.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // starts with no position .. all ok
        start = i;
        break;
      }
    }

    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
            && fractals.getTopFractalList().get(i) < candleList.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          // go long
          position = !position;
          open = candleList.get(i).getClose();
        }

      } else {
        if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
            && fractals.getDownFractalList().get(i) > candleList.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages,
              longPercentages);
          profitList.add(profit);
          position = !position;
        }
      }
    }

    if (position) {
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(),
          tradePercentages, longPercentages);
      profitList.add(profit);
    }

    t.setTradePercentages(tradePercentages);
    t.setLongPercentages(longPercentages);
    return t;
  }

  public AdxFractalDetailsTrades adxFractalDetails(int start, int adx, int adxLookback, int longThresh, int exitThresh,
      FractalsDefault fractals, List<Float> adxList, DI di, List<Candlestick> candleList) {
    float profit = 1;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    PositionDetails pos = new PositionDetails();
    float maxHigh = 0;
    LocalDateTime peakeDate = LocalDateTime.now();
    List<Details> detailsList = new ArrayList<>();
    boolean position = false;
    float open = 0.0f;

    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
          && fractals.getTopFractalList().get(i) < candleList.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // go long
        position = !position;
        open = candleList.get(i).getClose();
        pos.setDate(candleList.get(i).getDate());
        pos.setOpen(open);
        pos.setPosition(true);
        start = i;
        break;
      } else if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
          && fractals.getDownFractalList().get(i) > candleList.get(i).getClose()
          && adxList.get(i) > adxList.get(i - adxLookback)) {
        // starts with no position .. all ok
        start = i;
        break;
      }
    }

    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) > longThresh && di.getIsPositiveOver().get(i)
            && fractals.getTopFractalList().get(i) < candleList.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          // go long
          open = candleList.get(i).getClose();
          position = !position;
          pos.setDate(candleList.get(i).getDate());
          pos.setOpen(open);
          pos.setPosition(true);
          maxHigh = open;
          Details d = detailsList.get(detailsList.size() - 1);
          d.setFractalTop(fractals.getTopFractalList().get(i));
          d.setAdx(adxList.get(i));
        }

      } else {
        if (maxHigh < candleList.get(i).getClose()) {
          maxHigh = candleList.get(i).getClose();
          peakeDate = candleList.get(i).getDate();
        }
        if (adxList.get(i) > exitThresh && !di.getIsPositiveOver().get(i)
            && fractals.getDownFractalList().get(i) > candleList.get(i).getClose()
            && adxList.get(i) > adxList.get(i - adxLookback)) {
          profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages, candleList.get(i), pos,
              maxHigh, peakeDate, detailsList);
          profitList.add(profit);
          position = !position;
        }
      }
    }

    if (position) {
      profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages,
          candleList.get(candleList.size() - 1), pos, maxHigh, peakeDate, detailsList);
      profitList.add(profit);
    }

    AdxFractalDetailsTrades t = new AdxFractalDetailsTrades();
    t.setConf(stratUtils.calcConf(profitList));
    t.setAdx(adx);
    t.setLongThresh(longThresh);
    t.setExitThresh(exitThresh);
    t.setTradePercentages(tradePercentages);
    t.setLongPercentages(longPercentages);
    t.setLookback(adxLookback);
    t.setProfit(profit);
    t.setDetailsList(detailsList);
    return t;
  }

}
