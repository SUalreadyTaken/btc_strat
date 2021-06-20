package btc_strat.Utils;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;

public class AtrUtil {
  public AtrUtil() {
  }

  public List<Float> getAtr(int dayCount, float multiplier, List<Candlestick> candleList) {
    List<Float> tr = new ArrayList<>();
    tr.add(0F);
    // ------------ calc tr
    for (int i = 1; i < candleList.size(); i++) {
      // TR=Max[(H − L),Abs(H − CP),Abs(L − CP)]
      // can be simplified to TR = max(high, closeP) - min(low, closeP)
      tr.add(Math.max(candleList.get(i).getHigh(), candleList.get(i - 1).getClose())
          - Math.min(candleList.get(i).getLow(), candleList.get(i - 1).getClose()));
    }
    // ------------ calc atr
    List<Float> atr = new ArrayList<>();
    float total = 0;

    for (int i = 0; i < dayCount; i++) {
      total += tr.get(i);
      atr.add(total / (float) (i + 1));
    }

    int removeToBeDividedBy = dayCount - 1;
    for (int i = dayCount; i < candleList.size(); i++) {
      atr.add((atr.get(i - 1) * removeToBeDividedBy + tr.get(i)) / dayCount);
    }

    // ------------ calc Atr trailing stop
    List<Float> atrTrailing = new ArrayList<>();
    for (int i = 0; i < dayCount; i++) {
      atrTrailing.add(0F);
    }
    boolean isLong = false;
    float currentTrail = calcTail(atr.get(dayCount), multiplier, candleList.get(dayCount).getClose(), isLong);
    atrTrailing.add(currentTrail);
    for (int i = dayCount + 1; i < candleList.size(); i++) {
      if (isLong) {
        if (candleList.get(i).getClose() < currentTrail) {
          // stop hit on long
          isLong = false;
          currentTrail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
        } else {
          float newTail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
          if (newTail > currentTrail) {
            currentTrail = newTail;
          }
        }
      } else {
        if (candleList.get(i).getClose() > currentTrail) {
          // stop hit on short
          isLong = true;
          currentTrail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
        } else {
          float newTail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
          if (newTail < currentTrail) {
            currentTrail = newTail;
          }
        }
      }
      // todo make a list with isLong and return it .. no need to check for close >
      // trailingStop
      atrTrailing.add(currentTrail);
    }
    return atrTrailing;
  }

  public List<Boolean> getAtrIsLong(int dayCount, float multiplier, List<Candlestick> candleList) {
    List<Float> tr = new ArrayList<>();
    tr.add(0F);
    // ------------ calc tr
    for (int i = 1; i < candleList.size(); i++) {
      // TR=Max[(H − L),Abs(H − CP),Abs(L − CP)]
      // can be simplified to TR = max(high, closeP) - min(low, closeP)
      tr.add(Math.max(candleList.get(i).getHigh(), candleList.get(i - 1).getClose())
          - Math.min(candleList.get(i).getLow(), candleList.get(i - 1).getClose()));
    }
    // ------------ calc atr
    List<Float> atr = new ArrayList<>();
    float total = 0;

    for (int i = 0; i < dayCount; i++) {
      total += tr.get(i);
      atr.add(total / (float) (i + 1));
    }

    int removeToBeDividedBy = dayCount - 1;
    for (int i = dayCount; i < candleList.size(); i++) {
      atr.add((atr.get(i - 1) * removeToBeDividedBy + tr.get(i)) / dayCount);
    }

    // ------------ calc Atr trailing stop
    List<Boolean> result = new ArrayList<>();
    List<Float> atrTrailing = new ArrayList<>();
    for (int i = 0; i < dayCount; i++) {
      atrTrailing.add(0F);
      result.add(false);
    }
    boolean isLong = false;
    float currentTrail = calcTail(atr.get(dayCount), multiplier, candleList.get(dayCount).getClose(), isLong);
    atrTrailing.add(currentTrail);
    result.add(false);
    for (int i = dayCount + 1; i < candleList.size(); i++) {
      if (isLong) {
        if (candleList.get(i).getClose() < currentTrail) {
          // stop hit on long
          isLong = false;
          currentTrail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
        } else {
          float newTail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
          if (newTail > currentTrail) {
            currentTrail = newTail;
          }
        }
      } else {
        if (candleList.get(i).getClose() > currentTrail) {
          // stop hit on short
          isLong = true;
          currentTrail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
        } else {
          float newTail = calcTail(atr.get(i), multiplier, candleList.get(i).getClose(), isLong);
          if (newTail < currentTrail) {
            currentTrail = newTail;
          }
        }
      }
      // todo make a list with isLong and return it .. no need to check for close >
      // trailingStop
      atrTrailing.add(currentTrail);
      result.add(isLong);
    }
    return result;
    // return atrTrailing;
  }

  private float calcTail(float atr, float multiplier, float close, boolean isLong) {
    return isLong ? close - (atr * multiplier) : close + (atr * multiplier);
  }

}