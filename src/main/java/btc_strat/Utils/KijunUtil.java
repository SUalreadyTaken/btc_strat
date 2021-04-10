package btc_strat.Utils;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.Kijun;

public class KijunUtil {

  public KijunUtil() {}

  public Kijun calculateKijun(List<Candlestick> candlestickList, int dayCount) {
    float high = 0;
    float low = 10000000;
    int highIndex = 0;
    int lowIndex = 0;
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
    
    for (int i =  dayCount + 1; i < candlestickList.size(); i++) {
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

      if (candlestickList.get(i).getHigh() >= high) {
        highIndex = dayCount - 1;
        high = candlestickList.get(i).getHigh();
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
      float w = (high + low) / 2;
      result.add(w);
    }
    List<Boolean> isOverCloseList = new ArrayList<>();
    for (int i = 0; i < result.size(); i++) {
      if (result.get(i) > candlestickList.get(i).getClose()) {
        isOverCloseList.add(true);
      } else {
        isOverCloseList.add(false);
      }
    }
    return new Kijun(result, isOverCloseList);
  }

}