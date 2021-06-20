package btc_strat.Utils;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.Fractal;
import btc_strat.Model.FractalsDefault;

public class FractalUtil {

  public FractalUtil() {
  }

  private class TmpFractal {
    float topF;
    float lowF;

    private TmpFractal(float topF, float lowF) {
      this.topF = topF;
      this.lowF = lowF;
    }

    private float getTopF() {
      return this.topF;
    }

    private float getLowF() {
      return this.lowF;
    }
  }

  /**
   * Seems alright.. not the most optimal approach
   * 
   * @param candleList
   * @return
   */
  public List<Fractal> getFractalsTrailing(List<Candlestick> candleList) {
    float topFractal = 0.0f;
    float lowFractal = 0.0f;
    boolean isPositive = false;
    List<Fractal> result = new ArrayList<>();
    List<TmpFractal> tmpFractalList = new ArrayList<>();
    result.add(new Fractal(isPositive, lowFractal));
    result.add(new Fractal(isPositive, lowFractal));
    result.add(new Fractal(isPositive, lowFractal));
    result.add(new Fractal(isPositive, lowFractal));
    result.add(new Fractal(isPositive, lowFractal));
    TmpFractal tmp = new TmpFractal(lowFractal, topFractal);
    tmpFractalList.add(tmp);
    tmpFractalList.add(tmp);
    tmpFractalList.add(tmp);
    tmpFractalList.add(tmp);
    tmpFractalList.add(tmp);

    for (int i = 5; i < candleList.size() - 2; i++) {
      // topFractal
      if (candleList.get(i - 4).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i - 3).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i - 1).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i).getHigh() < candleList.get(i - 2).getHigh()) {
        topFractal = candleList.get(i - 2).getHigh();
      }
      // lowFractal
      if (candleList.get(i - 4).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i - 3).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i - 1).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i).getLow() > candleList.get(i - 2).getLow()) {
        lowFractal = candleList.get(i - 2).getLow();
      }
      tmpFractalList.add(new TmpFractal(topFractal, lowFractal));
    }

    int start = 0;
    // true == long || false = short
    boolean trend = false;
    for (int i = 5; i < candleList.size() - 2; i++) {
      if (tmpFractalList.get(i).getLowF() != 0 && candleList.get(i).getClose() > tmpFractalList.get(i).getLowF()) {
        trend = false;
        start = i;
        break;
      }
      if (tmpFractalList.get(i).getTopF() != 0 && candleList.get(i).getClose() < tmpFractalList.get(i).getTopF()) {
        trend = true;
        start = i;
        break;
      }
      result.add(new Fractal(false, 0.0f));
    }
    float trendTop = tmpFractalList.get(start).getTopF();
    float trendLow = tmpFractalList.get(start).getLowF();
    for (int i = start; i < tmpFractalList.size(); i++) {
      if (!trend) {
        if (tmpFractalList.get(i).getLowF() > trendLow) {
          trendLow = tmpFractalList.get(i).getLowF();
        }
        if (candleList.get(i).getClose() > trendLow) {
          result.add(new Fractal(false, trendLow));
        } else {
          trend = !trend;
          trendTop = tmpFractalList.get(i).getTopF();
          result.add(new Fractal(true, trendTop));
        }
      } else {
        if (tmpFractalList.get(i).getTopF() < trendTop) {
          trendTop = tmpFractalList.get(i).getTopF();
        }
        if (candleList.get(i).getClose() < trendTop) {
          result.add(new Fractal(true, trendTop));
        } else {
          trend = !trend;
          trendLow = tmpFractalList.get(i).getLowF();
          result.add(new Fractal(false, trendLow));
        }
      }
    }
    TmpFractal t = tmpFractalList.get(tmpFractalList.size() - 1);
    if (trend) {
      result.add(new Fractal(trend, t.getLowF()));
      result.add(new Fractal(trend, t.getLowF()));
    } else {
      result.add(new Fractal(trend, t.getTopF()));
      result.add(new Fractal(trend, t.getTopF()));
    }

    return result;
  }

  public FractalsDefault getFractalsDefault(List<Candlestick> candleList) {
    float topFractal = 0.0f;
    float lowFractal = 0.0f;
    List<Float> topFractals = new ArrayList<>();
    List<Float> downFractals = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      topFractals.add(0.0f);
      downFractals.add(0.0f);
    }

    for (int i = 5; i < candleList.size() - 2; i++) {
      // topFractal
      if (candleList.get(i - 4).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i - 3).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i - 1).getHigh() < candleList.get(i - 2).getHigh()
          && candleList.get(i).getHigh() < candleList.get(i - 2).getHigh()) {
        topFractal = candleList.get(i - 2).getHigh();
      }
      // lowFractal
      if (candleList.get(i - 4).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i - 3).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i - 1).getLow() > candleList.get(i - 2).getLow()
          && candleList.get(i).getLow() > candleList.get(i - 2).getLow()) {
        lowFractal = candleList.get(i - 2).getLow();
      }
      topFractals.add(topFractal);
      downFractals.add(lowFractal);
    }
    for (int i = 0; i < 2; i++) {
      topFractals.add(topFractal);
      downFractals.add(lowFractal);
    }

    return new FractalsDefault(topFractals, downFractals);
  }
}
