package btc_strat.Strats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.Details;
import btc_strat.Model.NegativeDetail;
import btc_strat.Model.Position;
import btc_strat.Model.PositionDetails;

public class StratUtils {
  // Σ [(X – Xm) * (Y – Ym)] / √ [Σ (X – Xm)2 * Σ (Y – Ym)2]
  public float calcConf(List<Float> profitList) {
    float size = profitList.size();
    List<Float> xList = new ArrayList<>();
    float xSum = 0f;
    float ySum = 0f;

    for (int i = 0; i < profitList.size(); i++) {
      xList.add((float) (i + 1));
      xSum += (float) i + 1;
      ySum += profitList.get(i);
    }
    float xMean = xSum / size;
    float yMean = ySum / size;

    float firstHalf = 0f;
    for (int i = 0; i < profitList.size(); i++) {
      firstHalf += (xList.get(i) - xMean) * (profitList.get(i) - yMean);
    }

    float secondFirst = 0f;
    float secondSecond = 0f;
    for (int i = 0; i < profitList.size(); i++) {
      secondFirst += (xList.get(i) - xMean) * (xList.get(i) - xMean);
      secondSecond += (profitList.get(i) - yMean) * (profitList.get(i) - yMean);
    }
    float secondHalf = (float) Math.sqrt(secondFirst * secondSecond);
    return (firstHalf / secondHalf) * (firstHalf / secondHalf);
  }

  public float closeLongProfit(float profit, float open, float close) {
    float percentage = (close - open) / open;
    return ((float) (1 + (percentage - 0.0052))) * profit;
  }

  public float closeShortProfit(float profit, float open, float close) {
    float percentage = (close - open) / open;
    return ((float) (1 - (percentage + 0.0052))) * profit;
  }

  public float closeLongTrade(float profit, float open, float close, List<Float> tradePercentages,
      List<Float> longPercentages) {
    float percentage = (close - open) / open;
    tradePercentages.add((percentage * 100) - 0.52f);
    longPercentages.add((percentage * 100) - 0.52f);
    return ((float) (1 + (percentage - 0.0052))) * profit;
  }

  public float closeShortTrade(float profit, float open, float close, List<Float> tradePercentages,
      List<Float> shortPercentages) {
    float percentage = (close - open) / open;
    tradePercentages.add(-((percentage * 100) - 0.52f));
    shortPercentages.add(-((percentage * 100) - 0.52f));
    return ((float) (1 - (percentage + 0.0052))) * profit;
  }

  public float closeLongExtraIterableProfit(int maxPortion, float profit, float close, List<Position> positionList) {

    float tmpProfit = profit;
    for (int i = 0; i < positionList.size(); i++) {
      Position pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      tmpProfit = ((float) (1 + (percentage - 0.0052))) * tmpProfit;
    }
    positionList.clear();
    return tmpProfit;
  }

  public float closeShortExtraIterableProfit(int maxPortion, float profit, float close, List<Position> positionList) {

    float tmpProfit = profit;
    for (int i = 0; i < positionList.size(); i++) {
      Position pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      tmpProfit = ((float) (1 - (percentage + 0.0052))) * tmpProfit;
    }
    positionList.clear();
    return tmpProfit;
  }

  public float closeLongExtraIterableTrades(int maxPortion, float profit, float close, List<Float> tradePercentages,
      List<Float> longPercentages, List<Position> positionList) {

    float tmpProfit = profit;
    for (int i = 0; i < positionList.size(); i++) {
      Position pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      tradePercentages.add((percentage * 100) - 0.52f);
      longPercentages.add((percentage * 100) - 0.52f);
      tmpProfit = ((float) (1 + (percentage - 0.0052))) * tmpProfit;
    }
    positionList.clear();
    ;
    return tmpProfit;
  }

  public float closeShortExtraIterableTrades(int maxPortion, float profit, float close, List<Float> tradePercentages,
      List<Float> shortPercentages, List<Position> positionList) {

    float tmpProfit = profit;
    for (int i = 0; i < positionList.size(); i++) {
      Position pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      tradePercentages.add(-(0.52f + (percentage * 100)));
      shortPercentages.add(-(0.52f + (percentage * 100)));
      tmpProfit = ((float) (1 - (percentage + 0.0052))) * tmpProfit;
    }
    positionList.clear();
    return tmpProfit;
  }

  public float closeLongExtraIterableTradesDetails(int maxPortion, float profit, float close,
      List<Float> tradePercentages, List<Float> longPercentages, List<PositionDetails> positionList,
      List<Details> detailsList, Candlestick candle) {

    float tmpProfit = profit;
    float totalPercentage = 0;
    for (int i = 0; i < positionList.size(); i++) {
      PositionDetails pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      totalPercentage += percentage;

      tradePercentages.add((percentage * 100) - 0.52f);
      longPercentages.add((percentage * 100) - 0.52f);
    }
    tmpProfit = ((float) (1 + (totalPercentage - 0.0052))) * tmpProfit;
    for (int i = 0; i < positionList.size(); i++) {
      PositionDetails pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      detailsList
          .add(new Details(pos.getOpen(), close, true, pos.getDate(), candle.getDate(), tmpProfit, percentage * 100));
    }
    positionList.clear();
    return tmpProfit;
  }

  public float closeShortExtraIterableTradesDetails(int maxPortion, float profit, float close,
      List<Float> tradePercentages, List<Float> shortPercentages, List<PositionDetails> positionList,
      List<Details> detailsList, Candlestick candle) {

    float tmpProfit = profit;
    float totalPercentage = 0;
    for (int i = 0; i < positionList.size(); i++) {
      PositionDetails pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      tradePercentages.add(-((percentage * 100) + 0.52f));
      shortPercentages.add(-((percentage * 100) + 0.52f));
      totalPercentage += percentage;
    }

    tmpProfit = ((float) (1 - (totalPercentage + 0.0052))) * tmpProfit;
    for (int i = 0; i < positionList.size(); i++) {
      PositionDetails pos = positionList.get(i);
      float percentage = ((close - pos.getOpen()) / pos.getOpen()) / maxPortion;
      detailsList.add(
          new Details(pos.getOpen(), close, false, pos.getDate(), candle.getDate(), tmpProfit, -(percentage * 100)));
    }
    positionList.clear();
    return tmpProfit;
  }

  public float closeLongDetail(float profit, float open, List<Float> tradePercentages, List<Float> longPercentages,
      Candlestick candle, PositionDetails pos, float maxHigh, LocalDateTime peakeDate, List<Details> detailsList) {

    float tmpProfit = profit;
    float percentage = (candle.getClose() - pos.getOpen()) / pos.getOpen();
    tradePercentages.add((percentage * 100) - 0.52f);
    longPercentages.add((percentage * 100) - 0.52f);
    tmpProfit = ((float) (1 + (percentage - 0.0052))) * tmpProfit;
    // todo whereever i use it i could just save the open date.. no need to make a
    // PositionDetails
    Details d = new Details(pos.getOpen(), candle.getClose(), true, pos.getDate(), candle.getDate(), tmpProfit,
        percentage * 100);
    d.setCouldTake(((maxHigh - pos.getOpen()) / pos.getOpen()) * 100);
    d.setPeakeDate(peakeDate);
    detailsList.add(d);

    return tmpProfit;
  }

  public float lowestPoint(int position, Candlestick candle, float open, float lowest) {
    if (position == 1) {
      // long get low
      float tmpLowest = candle.getLow() / open;
      if (tmpLowest < lowest) {
        lowest = tmpLowest;
      }
    } else {
      float tmpLowest = Math.abs((candle.getHigh() - open) / open);
      if (tmpLowest < lowest) {
        lowest = tmpLowest;
      }
    }
    return lowest;
  }

  public float lowestPointIter(int position, Candlestick candle, List<Position> openPositions, float lowest) {
    if (position == 1) {
      // long get low
      for (int i = 0; i < openPositions.size(); i++) {
        if (candle.getLow() < openPositions.get(i).getOpen()) {
          float tmpLowest = (openPositions.get(i).getOpen() - candle.getLow()) / openPositions.get(i).getOpen();
          if (tmpLowest > lowest) {
            lowest = tmpLowest;
          }
        }
      }
    } else {
      for (int i = 0; i < openPositions.size(); i++) {
        if (candle.getHigh() > openPositions.get(i).getOpen()) {
          float tmpLowest = (candle.getHigh() - openPositions.get(i).getOpen()) / openPositions.get(i).getOpen();
          if (tmpLowest > lowest) {
            lowest = tmpLowest;
          }
        }
      }
    }
    return lowest;
  }

  public float lowestPointIterTop5(int position, Candlestick candle, List<PositionDetails> openPositions, float lowest,
      List<NegativeDetail> top5) {
    if (candle.getDate().getYear() == 2020) {
      if (position == 1) {
        // long get low
        for (int i = 0; i < openPositions.size(); i++) {
          if (candle.getLow() < openPositions.get(i).getOpen()) {
            float tmpLowest = (openPositions.get(i).getOpen() - candle.getLow()) / openPositions.get(i).getOpen();
            if (tmpLowest > 0.05) {
              // if (tmpLowest > lowest) {
              lowest = tmpLowest;
              top5.add(new NegativeDetail(tmpLowest, candle.getDate()));
            }
          }
        }
      } else {
        for (int i = 0; i < openPositions.size(); i++) {
          if (candle.getHigh() > openPositions.get(i).getOpen()) {
            float tmpLowest = (candle.getHigh() - openPositions.get(i).getOpen()) / openPositions.get(i).getOpen();
            if (tmpLowest > 0.05) {
              // if (tmpLowest > lowest) {
              lowest = tmpLowest;
              top5.add(new NegativeDetail(tmpLowest, candle.getDate()));
            }
          }
        }
      }
    }
    return lowest;
  }

  public float closeLongExtra(float profit, float close, float open, List<Float> tradePercentages,
      List<Float> longPercentages) {

    float percentage = ((close - open) / open);
    tradePercentages.add(percentage * 100);
    longPercentages.add(percentage * 100);
    return (float) (1 + (percentage - 0.00075)) * profit;
  }

  public float closeShortExtra(float profit, float close, float open, List<Float> tradePercentages,
      List<Float> shortPercentages) {

    float percentage = ((close - open) / open);
    tradePercentages.add(-(percentage * 100));
    shortPercentages.add(-(percentage * 100));
    return (float) (1 - (percentage + 0.00075)) * profit;
  }
}
