package btc_strat.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;

/**
 * ToRSI
 */
public class MixUtil {


    public MixUtil() {
    }

    public List<Float> getRsiList(float dayCount, List<Candlestick> candlestickList) {
        float cGainEMA = 0;
        float cLossEMA = 0;
        float tmpOpen = candlestickList.get(0).getOpen();
        float tmpClose = candlestickList.get(0).getClose();
        if (tmpClose > tmpOpen) {
            cGainEMA = tmpClose - tmpOpen;
        } else if (tmpClose < tmpOpen) {
            cLossEMA = Math.abs(tmpClose - tmpOpen);
        }
        List<Float> result = new ArrayList<Float>();
        result.add(0F);
        // TODO for 1h and 15min strategy.. need to make a list of cGainEma and cLossEma for t-1
        // t aka gain/loss = 15min open/close
        for (int i = 1; i < candlestickList.size(); i++) {
            float close = candlestickList.get(i).getClose();
            float open = candlestickList.get(i).getOpen();
            float gain = 0;
            float loss = 0;
            if (close > open) {
                gain = close - open;
            } else if (close < open) {
                loss = Math.abs(close - open);
            }
            float avgU = (1 / dayCount) * gain + ((dayCount - 1) / dayCount) * cGainEMA;
            float avgD = (1 / dayCount) * loss + ((dayCount - 1) / dayCount) * cLossEMA;
            cGainEMA = avgU;
            cLossEMA = avgD;
            float rs = avgU / avgD;
            float rsi = 100 - 100 / (1 + rs);
            result.add(rsi);
        }
        return result;
    }

    public List<Double> getEmaList(int dayCount, List<Candlestick> candlestickList) {
        List<Double> result = new ArrayList<Double>();
        float weight = 2 / ((float) dayCount + 1);
        float tmpMa = 0;
        for (int i = 0; i < dayCount - 1; i++) {
            result.add(0.0);
            tmpMa += candlestickList.get(i).getClose();
        }
        tmpMa += candlestickList.get(dayCount - 1).getClose();
        result.add(BigDecimal.valueOf((tmpMa / dayCount)).setScale(1, RoundingMode.HALF_UP)
                .doubleValue());
        for (int i = dayCount; i < candlestickList.size(); i++) {
            double ema = BigDecimal.valueOf(
                    (candlestickList.get(i).getClose() * weight + result.get(i - 1) * (1 - weight)))
                    .setScale(1, RoundingMode.HALF_UP).doubleValue();
            result.add(ema);
        }
        return result;
    }

    public List<Float> getWilliamList(int dayCount, List<Candlestick> candlestickList) {
        // (HH - Close) / (HH - LL)
        int highIndex = 0;
        int lowIndex = 0;
        float high = 0;
        float low = 10000000;
        List<Float> result = new ArrayList<>();
        for (int i = 0; i <= dayCount; i++) {
            if (candlestickList.get(i).getHigh() > high) {
                high = candlestickList.get(i).getHigh();
                highIndex = Math.abs(i - dayCount);
            }
            if (candlestickList.get(i).getLow() < low) {
                low = candlestickList.get(i).getLow();
                lowIndex = Math.abs(i - dayCount);
            }
            result.add(0F);
        }
        // last (dayCount candles - 1) + current.
        for (int i = dayCount + 1; i < candlestickList.size(); i++) {
            if (candlestickList.get(i).getHigh() >= high) {
                high = candlestickList.get(i).getHigh();
                highIndex = dayCount - 1;
            } else {
                highIndex = highIndex - 1;
                if (highIndex <= 0) {
                    float tmpHigh = 0;
                    for (int j = i; j > i - dayCount; j--) {
                        if (candlestickList.get(j).getHigh() >= tmpHigh) {
                            tmpHigh = candlestickList.get(j).getHigh();
                            highIndex = dayCount - (i - j);
                        }
                    }
                    high = tmpHigh;
                }
            }
            if (candlestickList.get(i).getLow() <= low) {
                low = candlestickList.get(i).getLow();
                lowIndex = dayCount - 1;
            } else {
                lowIndex = lowIndex - 1;
                if (lowIndex <= 0) {
                    float tmpLow = 9999999;
                    for (int j = i; j > i - dayCount; j--) {
                        if (candlestickList.get(j).getLow() <= tmpLow) {
                            tmpLow = candlestickList.get(j).getLow();
                            lowIndex = dayCount - (i - j);
                        }
                    }
                    low = tmpLow;
                }
            }
            float w = ((high - candlestickList.get(i).getClose()) / (high - low)) * -100;
            result.add(w);
        }
        return result;
    }

    public List<Float> getWilliamList100(int dayCount, List<Candlestick> candlestickList) {
      // (HH - Close) / (HH - LL)
      int highIndex = 0;
      int lowIndex = 0;
      float high = 0;
      float low = 10000000;
      List<Float> result = new ArrayList<>();
      for (int i = 0; i <= dayCount; i++) {
          if (candlestickList.get(i).getHigh() > high) {
              high = candlestickList.get(i).getHigh();
              highIndex = Math.abs(i - dayCount);
          }
          if (candlestickList.get(i).getLow() < low) {
              low = candlestickList.get(i).getLow();
              lowIndex = Math.abs(i - dayCount);
          }
          result.add(0F);
      }
      // last (dayCount candles - 1) + current.
      for (int i = dayCount + 1; i < candlestickList.size(); i++) {
          if (candlestickList.get(i).getHigh() >= high) {
              high = candlestickList.get(i).getHigh();
              highIndex = dayCount - 1;
          } else {
              highIndex = highIndex - 1;
              if (highIndex <= 0) {
                  float tmpHigh = 0;
                  for (int j = i; j > i - dayCount; j--) {
                      if (candlestickList.get(j).getHigh() >= tmpHigh) {
                          tmpHigh = candlestickList.get(j).getHigh();
                          highIndex = dayCount - (i - j);
                      }
                  }
                  high = tmpHigh;
              }
          }
          if (candlestickList.get(i).getLow() <= low) {
              low = candlestickList.get(i).getLow();
              lowIndex = dayCount - 1;
          } else {
              lowIndex = lowIndex - 1;
              if (lowIndex <= 0) {
                  float tmpLow = 9999999;
                  for (int j = i; j > i - dayCount; j--) {
                      if (candlestickList.get(j).getLow() <= tmpLow) {
                          tmpLow = candlestickList.get(j).getLow();
                          lowIndex = dayCount - (i - j);
                      }
                  }
                  low = tmpLow;
              }
          }
          float w = ((high - candlestickList.get(i).getClose()) / (high - low)) * -100;
          result.add(w + 100);
      }
      return result;
  }

}
