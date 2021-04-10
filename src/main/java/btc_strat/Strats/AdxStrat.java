package btc_strat.Strats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import btc_strat.Model.Canal;
import btc_strat.Model.Candlestick;
import btc_strat.Model.DI;
import btc_strat.Model.Details;
import btc_strat.Model.Kijun;
import btc_strat.Model.NegativeDetail;
import btc_strat.Model.Position;
import btc_strat.Model.PositionDetails;
import btc_strat.Model.Trades;

public class AdxStrat {
  StratUtils stratUtils = new StratUtils();
  public AdxStrat() {
  }

  // cancal exit profit list start
  public List<Float> canalAdxExitProfitList(List<Float> wList, int canalLookback, int adxLookback, int kijunCount,
      Canal canal, Kijun kijun, int threshold, int exitThresh, int adxCount, List<Float> adxList, DI di,
      List<Candlestick> candleList) {
    List<Integer> startList = new ArrayList<>();
    startList.add(canalLookback + 1);
    startList.add(adxLookback + 1);
    startList.add(kijunCount + 1);
    startList.add((adxCount * 20) + 1);
    int start = Collections.max(startList);

    float profit = 1;
    int position = 1;
    List<Float> profitList = new ArrayList<>();
    float open = 0f;
    boolean signal = false;

    for (int i = start; i < candleList.size(); i++) {
      if (signal) {
        if (position == 1) {
          if (wList.get(i) < 50) {
            start = i;
            open = candleList.get(i).getClose();
            // isOpen = true;
            signal = false;
            break;
          }
        } else {
          if (wList.get(i) > 50) {
            start = i;
            open = candleList.get(i).getClose();
            // isOpen = true;
            signal = false;
            break;
          }
        }
      } else {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          // if so start positive
          position = 1;
          signal = true;
        } else if (adxList.get(i) > exitThresh && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          position = -1;
          signal = true;
        }
      }
    }

    for (int i = start + 1; i < candleList.size(); i++) {
      // if (isOpen) {
      if (position == -1) {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) < 50) {
            // go long
            signal = false;
            // isOpen = true;
            open = candleList.get(i).getClose();
            position = 1;
            continue;
          }
          if (adxList.get(i) > exitThresh && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
            signal = false;
          }
        }
      } else {
        if (adxList.get(i) > exitThresh && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) > 50) {
            // close long
            signal = false;
            // isOpen = false;
            position = -1;
            profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
            profitList.add(profit);
            continue;
          }
          if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
            signal = false;
          }
        }
      }
      // }
    }

   
    if (position == 1) {
      profit = stratUtils.closeLongProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    }

    return profitList;
  }
  // canal exit profit list finish

  // cancal exit trades start
  public Trades canalAdxExitTrades(List<Float> wList, int canalLookback, int adxLookback, int kijunCount, Canal canal,
      Kijun kijun, int threshold, int exitThres, int adxCount, List<Float> adxList, DI di,
      List<Candlestick> candleList) {
    List<Integer> startList = new ArrayList<>();
    startList.add(canalLookback + 1);
    startList.add(adxLookback + 1);
    startList.add(kijunCount + 1);
    startList.add((adxCount * 20) + 1);
    int start = Collections.max(startList);

    float profit = 1;
    int position = 1;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    float open = 0f;
    boolean signal = false;

    for (int i = start; i < candleList.size(); i++) {
      if (signal) {
        if (position == 1) {
          if (wList.get(i) < 50) {
            start = i;
            open = candleList.get(i).getClose();
            // isOpen = true;
            signal = false;
            break;
          }
        } else {
          if (wList.get(i) > 50) {
            start = i;
            open = candleList.get(i).getClose();
            // isOpen = true;
            signal = false;
            break;
          }
        }
      } else {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          // if so start positive
          position = 1;
          signal = true;
        } else if (adxList.get(i) > exitThres && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          // if so start short
          position = -1;
          signal = true;
        }
      }
    }

    for (int i = start + 1; i < candleList.size(); i++) {
      if (position == -1) {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) < 50) {
            // go long
            signal = false;
            // isOpen = true;
            open = candleList.get(i).getClose();
            position = 1;
            continue;
          }
          if (adxList.get(i) > exitThres && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
            signal = false;
          }
        }
      } else {
        if (adxList.get(i) > exitThres && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) > 50) {
            // close long
            signal = false;
            // isOpen = false;
            position = -1;
            profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages, longPercentages);
            profitList.add(profit);
            continue;
          }
          if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
            signal = false;
          }
        }
      }
    }

    if (position == 1) {
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(), tradePercentages,
          longPercentages);
      profitList.add(profit);
    }

    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setAdxMinThreshold(exitThres);
    result.setCanalLookback(canalLookback);
    result.setAdxLookback(adxLookback);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    return result;
  }
  // canal exit trades finish

  // cancal exit trades start
  public Trades canalAdxExitDetails(List<Float> wList, int canalLookback, int adxLookback, int kijunCount, Canal canal,
      Kijun kijun, int threshold, int exitThres, int adxCount, List<Float> adxList, DI di,
      List<Candlestick> candleList) {
    List<Integer> startList = new ArrayList<>();
    startList.add(canalLookback + 1);
    startList.add(adxLookback + 1);
    startList.add(kijunCount + 1);
    startList.add((adxCount * 20) + 1);
    int start = Collections.max(startList);

    float profit = 1;
    int position = 1;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    List<Details> detailsList = new ArrayList<>();
    PositionDetails pos = new PositionDetails();
    float open = 0f;
    float maxHigh = 0;
    LocalDateTime peakeDate = LocalDateTime.now();
    boolean signal = false;

    for (int i = start; i < candleList.size(); i++) {
      if (signal) {
        if (position == 1) {
          if (wList.get(i) < 50) {
            start = i;
            open = candleList.get(i).getClose();
            pos.setDate(candleList.get(i).getDate());
            pos.setOpen(open);
            pos.setPosition(true);
            // isOpen = true;
            signal = false;
            break;
          }
        } else {
          if (wList.get(i) > 50) {
            start = i;
            open = candleList.get(i).getClose();
            // isOpen = true;
            signal = false;
            break;
          }
        }
      } else {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          // if so start positive
          position = 1;
          signal = true;
        } else if (adxList.get(i) > exitThres && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          // if so start short
          position = -1;
          signal = true;
        }
      }
    }

    for (int i = start + 1; i < candleList.size(); i++) {
      if (position == -1) {
        if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) < 50) {
            // go long
            signal = false;
            // isOpen = true;
            open = candleList.get(i).getClose();
            pos.setDate(candleList.get(i).getDate());
            pos.setOpen(open);
            pos.setPosition(true);
            maxHigh = open;
            position = 1;
          }
        }
      } else {
        if (maxHigh < candleList.get(i).getClose()) {
          maxHigh = candleList.get(i).getClose();
          peakeDate = candleList.get(i).getDate();
        }
        if (adxList.get(i) > exitThres && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
            && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
          signal = true;
        }
        if (signal) {
          if (wList.get(i) > 50) {
            // close long
            signal = false;
            // isOpen = false;
            position = -1;
            profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages, candleList.get(i), pos, maxHigh,
                peakeDate, detailsList);
            profitList.add(profit);
            // open = candleList.get(i).getClose();
          }
        }
      }
    }

    if (position == 1) {
      profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages, candleList.get(candleList.size() - 1),
          pos, maxHigh, peakeDate, detailsList);
      profitList.add(profit);
    }

    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setAdxMinThreshold(exitThres);
    result.setCanalLookback(canalLookback);
    result.setAdxLookback(adxLookback);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setDetailsList(detailsList);
    return result;
  }

  public List<Float> canalAdxProfitList(int canalLookback, int adxLookback, int kijunCount, Canal canal, Kijun kijun,
      int threshold, int adxCount, List<Float> adxList, DI di, List<Candlestick> candleList) {
    List<Integer> startList = new ArrayList<>();
    startList.add(canalLookback + 1);
    startList.add(adxLookback + 1);
    startList.add(kijunCount + 1);
    startList.add((adxCount * 20) + 1);
    int start = Collections.max(startList);

    float profit = 1;
    int position = 1;
    List<Float> profitList = new ArrayList<>();
    float open = 0f;

    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
          && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
        // if so start positive
        position = 1;
        start = i;
        open = candleList.get(i).getClose();
        break;
      } else if (adxList.get(i) > threshold && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
          && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
        // if so start short
        position = -1;
        start = i;
        open = candleList.get(i).getClose();
        break;
      }
    }

    for (int i = start + 1; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        if (position == -1) {
          if (candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
            // was short close it and go long
            position = 1;
            open = candleList.get(i).getClose();
          }
        } else {
          if (candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
            // was long close it and go short
            position = -1;
            profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
            profitList.add(profit);
            open = candleList.get(i).getClose();
          }
        }
      }
    }
    if (position == 1) {
      profit = stratUtils.closeLongProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    } 

    return profitList;
  }

  public Trades canalAdxTrades(int canalLookback, int adxLookback, int kijunCount, Canal canal, Kijun kijun,
      int threshold, int adxCount, List<Float> adxList, DI di, List<Candlestick> candleList) {
    List<Integer> startList = new ArrayList<>();
    startList.add(canalLookback + 1);
    startList.add(adxLookback + 1);
    startList.add(kijunCount + 1);
    startList.add((adxCount * 20) + 1);
    int start = Collections.max(startList);

    float profit = 1;
    int position = 1;
    List<Float> profitList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    float open = 0f;

    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold && candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
          && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
        // if so start positive
        position = 1;
        start = i;
        open = candleList.get(i).getClose();
        break;
      } else if (adxList.get(i) > threshold && candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
          && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
        // if so start short
        position = -1;
        start = i;
        open = candleList.get(i).getClose();
        break;
      }
    }

    for (int i = start + 1; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        if (position == -1) {
          if (candleList.get(i).getClose() > canal.getHigherCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && di.getIsPositiveOver().get(i)) {
            // was short close it and go long
            position = 1;
            open = candleList.get(i).getClose();
          }
        } else {
          if (candleList.get(i).getClose() < canal.getLowerCanalList().get(i)
              && adxList.get(i) > adxList.get((i - adxLookback)) && !di.getIsPositiveOver().get(i)) {
            // was long close it and go short
            position = -1;
            profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages, longPercentages);
            profitList.add(profit);
            open = candleList.get(i).getClose();
          }
        }
      }
    }

    if (position == 1) {
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(), tradePercentages,
          shortPercentages);
      profitList.add(profit);
    } 

    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setCanalLookback(canalLookback);
    result.setAdxLookback(adxLookback);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    return result;
  }

  public List<Float> adxTrailingProfitConf(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, int atrCount, List<Float> atrTrailingList1h, List<Candlestick> candleList,
      List<Candlestick> candleList1h) {
    int start = 0;
    if (kijunCount > ((adxCount * 20) + 1)) {
      start = kijunCount;
    } else if (atrCount * 2 > ((adxCount * 20) + 1)) {
      start = atrCount * 2;
    } else {
      start = ((adxCount * 20) + 1);
    }
    float profit = 1;
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 1;
    int currentPortion = 0;
    boolean empty = true;
    List<Position> positionList = new ArrayList<>();
    List<Float> profitList = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)
            && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
            profitList.add(profit);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)
            && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
            profitList.add(profit);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          if (position == -1) {
            // open short
            if (kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)
                && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(false, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          } else {
            // open long
            if (!kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)
                && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(true, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
    }

    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      } else {
        profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      }
      profitList.add(profit);
    }

    return profitList;
  }

  public Trades adxTrailingTradesConf(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, int atrCount, List<Float> atrTrailingList1h, List<Candlestick> candleList,
      List<Candlestick> candleList1h) {
    int start = 0;
    if (kijunCount > ((adxCount * 20) + 1)) {
      start = kijunCount;
    } else if (atrCount * 2 > ((adxCount * 20) + 1)) {
      start = atrCount * 2;
    } else {
      start = ((adxCount * 20) + 1);
    }
    float profit = 1;
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 1;
    int currentPortion = 0;
    boolean empty = true;
    List<Position> positionList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    List<Float> profitList = new ArrayList<>();
    float lowest = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)
            && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeLongExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                longPercentages, positionList);
            profitList.add(profit);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)
            && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeShortExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                shortPercentages, positionList);
            profitList.add(profit);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          if (position == -1) {
            // open short
            if (kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)
                && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(false, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          } else {
            // open long
            if (!kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)
                && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(true, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
    }

    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
        profitList.add(profit);
      } else {
        profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
        profitList.add(profit);
      }
    }
    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxMinThreshold(lowerThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setLowest(lowest);
    result.setAtr(atrCount);
    return result;
  }

  public Trades adxOnlyLongTradesConf(int kijunCount, Kijun kijun, int threshold, int closeLongThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    List<Float> profitList = new ArrayList<>();
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    List<Float> tradePercentages = new ArrayList<>();
    List<Float> longPercentages = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i)
            && kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages, longPercentages);
            profitList.add(profit);
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }

    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(), tradePercentages,
          longPercentages);
      profitList.add(profit);
    }
    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setCloseLongThreshold(closeLongThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    return result;
  }

  public List<Float> adxOnlyLongProfitList(int kijunCount, Kijun kijun, int threshold, int closeLongThreshold,
      int adxCount, List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    List<Float> profitList = new ArrayList<>();
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i)
            && kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
            profitList.add(profit);
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }
    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeLongProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
      profitList.add(profit);
    }

    return profitList;
  }

  public Trades adxOnlyLongDetails(int kijunCount, Kijun kijun, int threshold, int closeLongThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    PositionDetails pos = new PositionDetails();
    List<Details> detailsList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<>();
    List<Float> longPercentages = new ArrayList<>();
    float maxHigh = 0;
    LocalDateTime peakeDate = LocalDateTime.now();
    List<Float> profitList = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        pos.setDate(candleList.get(i).getDate());
        pos.setOpen(open);
        pos.setPosition(position);
        break;
      }
      if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      // if (maxHigh < candleList.get(i).getHigh()) {
      // maxHigh = candleList.get(i).getHigh();
      // }
      if (maxHigh < candleList.get(i).getClose()) {
        maxHigh = candleList.get(i).getClose();
        peakeDate = candleList.get(i).getDate();
      }
      if (!position) {
        if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
          pos.setDate(candleList.get(i).getDate());
          pos.setOpen(open);
          pos.setPosition(position);
          maxHigh = open;
        }

      } else {
        if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i)
            && kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages, candleList.get(i), pos, maxHigh,
                peakeDate, detailsList);
            profitList.add(profit);
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }
    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeLongDetail(profit, open, tradePercentages, longPercentages, candleList.get(candleList.size() - 1),
          pos, maxHigh, peakeDate, detailsList);
      profitList.add(profit);
    }
    Trades result = new Trades();
    float conf = stratUtils.calcConf(profitList);
    result.setConf(conf);
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setCloseLongThreshold(closeLongThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setDetailsList(detailsList);
    return result;
  }

  public float adxOnlyLongProfit(int kijunCount, Kijun kijun, int threshold, int closeLongThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i)
            && kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeLongProfit(profit, open, candleList.get(i).getClose());
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }
    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeLongProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
    }

    return profit;
  }

  public Trades adxOnlyLongTrades(int kijunCount, Kijun kijun, int threshold, int closeLongThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    List<Float> tradePercentages = new ArrayList<>();
    List<Float> longPercentages = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && di.getIsPositiveOver().get(i) && !kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeLongThreshold && !di.getIsPositiveOver().get(i)
            && kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeLongTrade(profit, open, candleList.get(i).getClose(), tradePercentages, longPercentages);
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }

    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeLongTrade(profit, open, candleList.get(candleList.size() - 1).getClose(), tradePercentages,
          longPercentages);
    }
    Trades result = new Trades();
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setCloseLongThreshold(closeLongThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    return result;
  }

  public float adxOnlyShortProfit(int kijunCount, Kijun kijun, int threshold, int closeShortThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeShortThreshold && di.getIsPositiveOver().get(i)
          && !kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
          // go short
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeShortThreshold && di.getIsPositiveOver().get(i)
            && !kijun.getIsOverCloseList().get(i)) {
          // close short
          if (!empty) {
            profit = stratUtils.closeShortProfit(profit, open, candleList.get(i).getClose());
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }
    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeShortProfit(profit, open, candleList.get(candleList.size() - 1).getClose());
    }
    return profit;
  }

  public Trades adxOnlyShortTrades(int kijunCount, Kijun kijun, int threshold, int closeShortThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    int start = kijunCount > adxCount ? kijunCount * 10 : adxCount * 10;
    float profit = 1;
    boolean empty = true;
    boolean position = false;
    float open = 0f;
    List<Float> tradePercentages = new ArrayList<>();
    List<Float> shortPercentages = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) >= threshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
        position = true;
        open = candleList.get(i).getClose();
        start = i < candleList.size() - 2 ? i + 1 : i;
        empty = !empty;
        break;
      }
      if (adxList.get(i) >= closeShortThreshold && di.getIsPositiveOver().get(i)
          && !kijun.getIsOverCloseList().get(i)) {
        start = i < candleList.size() - 2 ? i + 1 : i;
        break;
      }
    }
    for (int i = start; i < candleList.size(); i++) {
      if (!position) {
        if (adxList.get(i) >= threshold && !di.getIsPositiveOver().get(i) && kijun.getIsOverCloseList().get(i)) {
          // go long
          open = candleList.get(i).getClose();
          position = true;
        }

      } else {
        if (adxList.get(i) >= closeShortThreshold && di.getIsPositiveOver().get(i)
            && !kijun.getIsOverCloseList().get(i)) {
          // close long
          if (!empty) {
            profit = stratUtils.closeShortTrade(profit, open, candleList.get(i).getClose(), tradePercentages, shortPercentages);
          } else {
            empty = !empty;
          }
          position = false;
        }
      }
    }

    if (position && !empty) {
      // should always be true
      profit = stratUtils.closeShortTrade(profit, open, candleList.get(candleList.size() - 1).getClose(), tradePercentages,
          shortPercentages);
    }
    Trades result = new Trades();
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setCloseShortThreshold(closeShortThreshold);
    result.setAdxThreshold(threshold);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    return result;
  }

  /**
   * no trade count atm only profit
   */
  public float adxTrailingProfit(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, int atrCount, List<Float> atrTrailingList1h, List<Candlestick> candleList,
      List<Candlestick> candleList1h) {
    int start = 0;
    if (kijunCount > ((adxCount * 20) + 1)) {
      start = kijunCount;
    } else if (atrCount * 2 > ((adxCount * 20) + 1)) {
      start = atrCount * 2;
    } else {
      start = ((adxCount * 20) + 1);
    }
    float profit = 1;
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 3;
    int currentPortion = 0;
    boolean empty = true;
    List<Position> positionList = new ArrayList<>();
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)
            && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)
            && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          if (position == -1) {
            // open short
            if (kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)
                && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(false, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          } else {
            // open long
            if (!kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)
                && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(true, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
    }

    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      } else {
        profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      }
    }

    return profit;
  }

  public Trades adxTrailingTrades(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, int atrCount, List<Float> atrTrailingList1h, List<Candlestick> candleList,
      List<Candlestick> candleList1h) {
    int start = 0;
    if (kijunCount > ((adxCount * 20) + 1)) {
      start = kijunCount;
    } else if (atrCount * 2 > ((adxCount * 20) + 1)) {
      start = atrCount * 2;
    } else {
      start = ((adxCount * 20) + 1);
    }
    float profit = 1;
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 3;
    int currentPortion = 0;
    boolean empty = true;
    List<Position> positionList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    float lowest = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)
            && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeLongExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                longPercentages, positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)
            && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeShortExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                shortPercentages, positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          if (position == -1) {
            // open short
            if (kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)
                && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(false, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          } else {
            // open long
            if (!kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)
                && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new Position(true, candleList.get(i).getClose()));
              currentPortion++;
              canOpenPosition = 0;
            }
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
    }

    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      } else {
        profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      }
    }
    Trades result = new Trades();
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxMinThreshold(lowerThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setLowest(lowest);
    result.setAtr(atrCount);
    return result;
  }

  public Trades adxTrailingTradesDetails(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, int atrCount, List<Float> atrTrailingList1h, List<Candlestick> candleList,
      List<Candlestick> candleList1h) {
    int start = 0;
    if (kijunCount > ((adxCount * 20) + 1)) {
      start = kijunCount;
    } else if (atrCount * 2 > ((adxCount * 20) + 1)) {
      start = atrCount * 2;
    } else {
      start = ((adxCount * 20) + 1);
    }
    float profit = 1;
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 1;
    int currentPortion = 1;
    boolean empty = true;
    List<PositionDetails> positionList = new ArrayList<>();
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    List<Details> detailsList = new ArrayList<>();
    List<NegativeDetail> top5Negative = new ArrayList<>();
    float lowest = 0f;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)
            && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeLongExtraIterableTradesDetails(maxPortions, profit, candleList.get(i).getClose(),
                tradePercentages, longPercentages, positionList, detailsList, candleList.get(i));
          } else {
            empty = false;
          }
          positionList.add(new PositionDetails(false, candleList.get(i).getClose(), candleList.get(i).getDate()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)
            && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
          if (!empty) {
            profit = stratUtils.closeShortExtraIterableTradesDetails(maxPortions, profit, candleList.get(i).getClose(),
                tradePercentages, shortPercentages, positionList, detailsList, candleList.get(i));
          } else {
            empty = false;
          }
          positionList.add(new PositionDetails(true, candleList.get(i).getClose(), candleList.get(i).getDate()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          if (position == -1) {
            // open short
            if (kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)
                && !isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new PositionDetails(false, candleList.get(i).getClose(), candleList.get(i).getDate()));
              currentPortion++;
              canOpenPosition = 0;
            }
          } else {
            // open long
            if (!kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)
                && isPrev1hTrailingLong(atrTrailingList1h, i, candleList.get(i).getClose())) {
              positionList.add(new PositionDetails(true, candleList.get(i).getClose(), candleList.get(i).getDate()));
              currentPortion++;
              canOpenPosition = 0;
            }
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
      lowest = stratUtils.lowestPointIterTop5(position, candleList.get(i), positionList, lowest, top5Negative);
    }

    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableTradesDetails(maxPortions, profit,
            candleList.get(candleList.size() - 1).getClose(), tradePercentages, longPercentages, positionList,
            detailsList, candleList.get(candleList.size() - 1));
      } else {
        profit = stratUtils.closeShortExtraIterableTradesDetails(maxPortions, profit,
            candleList.get(candleList.size() - 1).getClose(), tradePercentages, longPercentages, positionList,
            detailsList, candleList.get(candleList.size() - 1));
      }
    }
    Trades result = new Trades();
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxMinThreshold(lowerThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setLowest(lowest);
    result.setAtr(atrCount);
    result.setDetailsList(detailsList);
    List<NegativeDetail> new5 = new ArrayList<>();
    top5Negative.sort(Comparator.comparing(NegativeDetail::getNegative));
    int ss = top5Negative.size() / 2;
    for (int j = 1; j < ss; j++) {
      new5.add(top5Negative.get(top5Negative.size() - j));
    }
    result.setTop5Negative(new5);
    return result;
  }

  private boolean isPrev1hTrailingLong(List<Float> atrTrailingList1h, int i, float close) {
    int index = (int) Math.floor((double) ((i / 4) - 1));
    return atrTrailingList1h.get(index) < close ? true : false;
  }

  public Trades adxThreshKijunProfit(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    // false = short | true = long
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 3;
    int currentPortion = 0;
    int start = kijunCount > ((adxCount * 20) + 1) ? kijunCount : ((adxCount * 20) + 1);
    float profit = 1;
    int tradeCount = 0;
    List<Position> positionList = new ArrayList<>();
    boolean empty = true;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)) {
          if (!empty) {
            tradeCount = tradeCount + positionList.size();
            profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)) {
          if (!empty) {
            tradeCount = tradeCount + positionList.size();
            profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(i).getClose(), positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          // open short
          if (position == -1 && kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)) {
            positionList.add(new Position(false, candleList.get(i).getClose()));
            currentPortion++;
            canOpenPosition = 0;
          }
          // open long
          else if (position == 1 && !kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)) {
            positionList.add(new Position(true, candleList.get(i).getClose()));
            currentPortion++;
            canOpenPosition = 0;
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
      // lowest = lowestPointIter(position, candleList.get(i), positionList, lowest);
    }
    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      } else {
        profit = stratUtils.closeShortExtraIterableProfit(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            positionList);
      }
    }
    Trades res = new Trades();
    res.setProfit(profit);
    res.setTradeCount(tradeCount);
    return res;

  }

  public Trades adxThreshKijunTrades(int kijunCount, Kijun kijun, int threshold, int lowerThreshold, int adxCount,
      List<Float> adxList, DI di, List<Candlestick> candleList) {
    // false = short | true = long
    int position = 1;
    int canOpenPosition = 0;
    // max leverage 3.. so int leverage <= 1
    // int leverage = 0;
    int maxPortions = 3;
    int currentPortion = 0;
    int start = kijunCount > ((adxCount * 20) + 1) ? kijunCount : ((adxCount * 20) + 1);
    float profit = 1;
    List<Float> tradePercentages = new ArrayList<Float>();
    List<Float> longPercentages = new ArrayList<Float>();
    List<Float> shortPercentages = new ArrayList<Float>();
    List<Position> positionList = new ArrayList<>();
    float lowest = 0;
    boolean empty = true;
    for (int i = start; i < candleList.size(); i++) {
      if (adxList.get(i) > threshold) {
        // long go short
        if (kijun.getIsOverCloseList().get(i) && position == 1 && !di.getIsPositiveOver().get(i)) {
          if (!empty) {
            // if (positionList.size() != 0) {
            profit = stratUtils.closeLongExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                longPercentages, positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(false, candleList.get(i).getClose()));
          position = -1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // short go long
        else if (!kijun.getIsOverCloseList().get(i) && position == -1 && di.getIsPositiveOver().get(i)) {
          if (!empty) {
            // if (positionList.size() != 0) {
            profit = stratUtils.closeShortExtraIterableTrades(maxPortions, profit, candleList.get(i).getClose(), tradePercentages,
                shortPercentages, positionList);
          } else {
            empty = false;
          }
          positionList.add(new Position(true, candleList.get(i).getClose()));
          position = 1;
          canOpenPosition = 0;
          currentPortion = 1;
        }
        // open more positions.. leverage
        else if (canOpenPosition == 1 && currentPortion < maxPortions) {
          // open short
          if (position == -1 && kijun.getIsOverCloseList().get(i) && !di.getIsPositiveOver().get(i)) {
            positionList.add(new Position(false, candleList.get(i).getClose()));
            currentPortion++;
            canOpenPosition = 0;
          }
          // open long
          else if (position == 1 && !kijun.getIsOverCloseList().get(i) && di.getIsPositiveOver().get(i)) {
            positionList.add(new Position(true, candleList.get(i).getClose()));
            currentPortion++;
            canOpenPosition = 0;
          }
        }
      }
      // go neutral.. leave position open
      else if (canOpenPosition == 0 && adxList.get(i) <= lowerThreshold) {
        canOpenPosition = 1;
      }
      // lowest = lowestPointIter(position, candleList.get(i), positionList, lowest);
    }
    if (!positionList.isEmpty()) {
      // should always be true
      if (position == 1) {
        profit = stratUtils.closeLongExtraIterableTrades(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            tradePercentages, longPercentages, positionList);
      } else {
        profit = stratUtils.closeShortExtraIterableTrades(maxPortions, profit, candleList.get(candleList.size() - 1).getClose(),
            tradePercentages, longPercentages, positionList);
      }
    }
    Trades result = new Trades();
    result.setAdx(adxCount);
    result.setKijun(kijunCount);
    result.setAdxMinThreshold(lowerThreshold);
    result.setAdxThreshold(threshold);
    result.setLongPercentages(longPercentages);
    result.setShortPercentages(shortPercentages);
    result.setTradePercentages(tradePercentages);
    result.setProfit(profit);
    result.setLowest(lowest);
    return result;

  }

}