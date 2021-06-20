package btc_strat.Utils;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.DI;

public class ADXutil {
  public List<Float> getAdx(int n, List<Candlestick> candlestickList) {
    List<Float> tr = new ArrayList<>();
    List<Float> atr = new ArrayList<>();
    List<Float> dxPos = new ArrayList<>();
    List<Float> dxNeg = new ArrayList<>();
    List<Float> smoothDxPos = new ArrayList<>();
    List<Float> smoothDxNeg = new ArrayList<>();
    List<Float> posDMI = new ArrayList<>();
    List<Float> negDMI = new ArrayList<>();
    List<Float> dx = new ArrayList<>();
    List<Float> adx = new ArrayList<>();
    tr.add(0F);
    dxPos.add(0F);
    dxNeg.add(0F);
    // add 0s
    for (int i = 0; i < n; i++) {
      atr.add(0F);
      smoothDxPos.add(0F);
      smoothDxNeg.add(0F);
      posDMI.add(0F);
      negDMI.add(0F);
      dx.add(0F);
    }

    for (int i = 1; i < candlestickList.size(); i++) {
      tr.add(Math.max(candlestickList.get(i).getHigh(), candlestickList.get(i - 1).getClose())
          - Math.min(candlestickList.get(i).getLow(), candlestickList.get(i - 1).getClose()));
    }

    float tmpTr = 0;
    for (int i = 0; i < n; i++) {
      tmpTr += tr.get(i);
    }
    atr.add((tmpTr / n));

    for (int i = n + 1; i < candlestickList.size(); i++) {
      atr.add((atr.get(i - 1) * (n - 1) + tr.get(i)) / n);
    }

    for (int i = 1; i < candlestickList.size(); i++) {
      float upMove = candlestickList.get(i).getHigh() - candlestickList.get(i - 1).getHigh();
      float downMove = candlestickList.get(i - 1).getLow() - candlestickList.get(i).getLow();
      if (upMove > 0 && upMove > downMove) {
        dxPos.add(upMove);
      } else {
        dxPos.add(0F);
      }
      if (downMove > 0 && downMove > upMove) {
        dxNeg.add(downMove);
      } else {
        dxNeg.add(0F);
      }
    }

    float tmpSmoothDxPos = 0F;
    float tmpSmoothDxNeg = 0F;
    for (int i = 1; i < n + 1; i++) {
      tmpSmoothDxPos += dxPos.get(i);
      tmpSmoothDxNeg += dxNeg.get(i);
    }
    smoothDxPos.add(tmpSmoothDxPos / n);
    smoothDxNeg.add(tmpSmoothDxNeg / n);
    posDMI.add((smoothDxPos.get(n) / atr.get(n)) * 100);
    negDMI.add((smoothDxNeg.get(n) / atr.get(n)) * 100);
    dx.add((Math.abs(posDMI.get(n) - negDMI.get(n)) / (posDMI.get(n) + negDMI.get(n))) * 100);
    // smoothDxPos(16..) = ((smoothDxPos(-1) * (n - 1)) + dxPos) / n
    for (int i = n + 1; i < candlestickList.size(); i++) {
      smoothDxPos.add((smoothDxPos.get(i - 1) * (n - 1) + dxPos.get(i)) / n);
      smoothDxNeg.add((smoothDxNeg.get(i - 1) * (n - 1) + dxNeg.get(i)) / n);
      posDMI.add((smoothDxPos.get(i) / atr.get(i)) * 100);
      negDMI.add((smoothDxNeg.get(i) / atr.get(i)) * 100);
      dx.add((Math.abs(posDMI.get(i) - negDMI.get(i)) / (posDMI.get(i) + negDMI.get(i))) * 100);
    }

    // adx(29) = avg(dx(15-28))
    for (int i = 0; i < n * 2; i++) {
      adx.add(0F);
    }
    float tmpAdx = 0F;
    for (int i = n; i < n * 2; i++) {
      tmpAdx += dx.get(i);
    }
    adx.add(tmpAdx / n);
    // adx(30) = (adx(29) * (n-1) + dx(30)) / n
    for (int i = (n * 2) + 1; i < candlestickList.size(); i++) {
      adx.add((adx.get(i - 1) * (n - 1) + dx.get(i)) / n);
    }
    return adx;

  }

  public DI getDI(int n, List<Candlestick> candlestickList) {
    List<Float> tr = new ArrayList<>();
    List<Float> atr = new ArrayList<>();
    List<Float> dxPos = new ArrayList<>();
    List<Float> dxNeg = new ArrayList<>();
    List<Float> smoothDxPos = new ArrayList<>();
    List<Float> smoothDxNeg = new ArrayList<>();
    List<Float> posDMI = new ArrayList<>();
    List<Float> negDMI = new ArrayList<>();
    List<Float> dx = new ArrayList<>();
    tr.add(0F);
    dxPos.add(0F);
    dxNeg.add(0F);
    // add 0s
    for (int i = 0; i < n; i++) {
      atr.add(0F);
      smoothDxPos.add(0F);
      smoothDxNeg.add(0F);
      posDMI.add(0F);
      negDMI.add(0F);
      dx.add(0F);
    }

    for (int i = 1; i < candlestickList.size(); i++) {
      tr.add(Math.max(candlestickList.get(i).getHigh(), candlestickList.get(i - 1).getClose())
          - Math.min(candlestickList.get(i).getLow(), candlestickList.get(i - 1).getClose()));
    }

    float tmpTr = 0;
    for (int i = 0; i < n; i++) {
      tmpTr += tr.get(i);
    }
    atr.add((tmpTr / n));

    for (int i = n + 1; i < candlestickList.size(); i++) {
      atr.add((atr.get(i - 1) * (n - 1) + tr.get(i)) / n);
    }

    for (int i = 1; i < candlestickList.size(); i++) {
      float upMove = candlestickList.get(i).getHigh() - candlestickList.get(i - 1).getHigh();
      float downMove = candlestickList.get(i - 1).getLow() - candlestickList.get(i).getLow();
      if (upMove > 0 && upMove > downMove) {
        dxPos.add(upMove);
      } else {
        dxPos.add(0F);
      }
      if (downMove > 0 && downMove > upMove) {
        dxNeg.add(downMove);
      } else {
        dxNeg.add(0F);
      }
    }

    float tmpSmoothDxPos = 0F;
    float tmpSmoothDxNeg = 0F;
    for (int i = 1; i < n + 1; i++) {
      tmpSmoothDxPos += dxPos.get(i);
      tmpSmoothDxNeg += dxNeg.get(i);
    }
    smoothDxPos.add(tmpSmoothDxPos / n);
    smoothDxNeg.add(tmpSmoothDxNeg / n);
    posDMI.add((smoothDxPos.get(n) / atr.get(n)) * 100);
    negDMI.add((smoothDxNeg.get(n) / atr.get(n)) * 100);
    dx.add((Math.abs(posDMI.get(n) - negDMI.get(n)) / (posDMI.get(n) + negDMI.get(n))) * 100);
    // smoothDxPos(16..) = ((smoothDxPos(-1) * (n - 1)) + dxPos) / n
    for (int i = n + 1; i < candlestickList.size(); i++) {
      smoothDxPos.add((smoothDxPos.get(i - 1) * (n - 1) + dxPos.get(i)) / n);
      smoothDxNeg.add((smoothDxNeg.get(i - 1) * (n - 1) + dxNeg.get(i)) / n);
      posDMI.add((smoothDxPos.get(i) / atr.get(i)) * 100);
      negDMI.add((smoothDxNeg.get(i) / atr.get(i)) * 100);
    }

    List<Boolean> positiveOver = new ArrayList<>();
    for (int i = 0; i < posDMI.size(); i++) {
      if (posDMI.get(i) > negDMI.get(i)) {
        positiveOver.add(true);
      } else {
        positiveOver.add(false);
      }
    }

    return new DI(posDMI, negDMI, positiveOver);
  }
}