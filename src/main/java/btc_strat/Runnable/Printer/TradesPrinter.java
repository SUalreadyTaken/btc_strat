package btc_strat.Runnable.Printer;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.MRBands;
import btc_strat.Model.MrResult;
import btc_strat.Model.RawData;
import btc_strat.Model.StratEnum;
import btc_strat.Model.Trades;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.MRBandsStrat;
import btc_strat.Utils.MRBandsUtil;

public class TradesPrinter implements Runnable {
  private BlockingQueue<Integer> queue;
  private List<Trades> bestList;
  private int poisonPill;
  private int howMany;
  private StratEnum strat;
  private RawData rawData;

  public TradesPrinter(BlockingQueue<Integer> queue, int howMany, int poisonPill) {
    this.queue = queue;
    this.howMany = howMany;
    this.poisonPill = poisonPill;
  }

  public List<Trades> getBestList() {
    return this.bestList;
  }

  public void setBestList(List<Trades> bestList) {
    this.bestList = bestList;
  }

  public StratEnum getStrat() {
    return this.strat;
  }

  public void setStrat(StratEnum strat) {
    this.strat = strat;
  }

  public RawData getRawData() {
    return this.rawData;
  }

  public void setRawData(RawData rawData) {
    this.rawData = rawData;
  }

  public void run() {
    long start = System.currentTimeMillis();
    // DecimalFormat df = new DecimalFormat("#.####");
    try {
      while (true) {
        int number = queue.take();
        if (number == 1) {
          this.poisonPill--;
          if (this.poisonPill == 0) {
            // this.bestKijunADXList.sort(Comparator.comparing(KijunADXResult::getPercentage));
            this.bestList.sort(Comparator.comparing(Trades::getProfit));
            this.howMany = this.howMany < this.bestList.size() ? this.howMany : this.bestList.size() - 1;
            switch (this.strat) {
            case ADXPORTION:
              adxPortion();
              break;
            case TRAILINGSTOP:
              trailingStop();
              break;
            case ADXTRAILING:
              adxTrailing();
              break;
            case ADXLONGONLY:
              adxLongOnly();
              break;
            case ADXSHORTONLY:
              adxShortOnly();
              break;
            case CANALADX:
              canalAdx();
              break;
            case MRBANDSLONG:
              mrbandsLong();
              break;
            default:
              break;
            }

            System.out.println("calc time > " + (System.currentTimeMillis() - start));
            return;
          }
        }

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void mrbandsLong() {
    MRBandsStrat mrbStrat = new MRBandsStrat();
    MRBandsUtil mrBandsUtil = new MRBandsUtil();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);
      List<MrResult> mrResultList = mrBandsUtil.getMrList(this.rawData.getCandleList(), t.getWindow(), t.getDegree());
      MRBands mrb = mrBandsUtil.getBands(mrResultList, t.getMult(), t.getWindow());
      // MRBands mrb, int window, List<Candlestick> candleList
      int tmpDegree = t.getDegree();
      float tmpMult = t.getMult();
      t = mrbStrat.mrbLongOnlyTrade(mrb, t.getWindow(), this.rawData.getCandleList());
      t.setDegree(tmpDegree);
      t.setMult(tmpMult);
      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }
      System.out.println("win:" + t.getWindow() + " | deg:" + t.getDegree() + " | mult:" + t.getMult() + " | pro:"
          + df.format(t.getProfit()) + " | conf:" + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:"
          + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | pL:"
          + profitableLongs + " | nL:" + negativeLongs);
    }

  }

  private void canalAdx() {
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);
      t = adxStrat.canalAdxExitTrades(this.rawData.getWList(),t.getCanalLookback(), t.getAdxLookback(), t.getKijun(), t.getCanal(),
          this.rawData.getKijunList().get(t.getKijun()), t.getAdxThreshold(), t.getAdxMinThreshold(), t.getAdx(),
          this.rawData.getAdxList().get(t.getAdx()), this.rawData.getDiList().get(t.getAdx()),
          this.rawData.getCandleList());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float profitableShorts = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }

      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
          + " | mThr:" + t.getAdxMinThreshold() + " |aL: " + t.getAdxLookback() + " |cL: " + (t.getCanalLookback() + 1)
          + " | pro:" + df.format(t.getProfit()) + " | conf:" + df.format(t.getConf()) + " | aP:" + df.format(avgP)
          + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | pL:"
          + profitableLongs + " | nL:" + negativeLongs);
    }
  }

  private void adxShortOnly() {
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);

      t = adxStrat.adxOnlyShortTrades(t.getKijun(), this.rawData.getKijunList().get(t.getKijun()), t.getAdxThreshold(),
          t.getCloseShortThreshold(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), this.rawData.getCandleList());
      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableShorts = 0;
      float negativeShorts = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getShortPercentages().size(); j++) {
        if (t.getShortPercentages().get(j) >= 0) {
          profitableShorts++;
        } else {
          negativeShorts++;
        }
      }
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
          + " | cThr:" + t.getCloseShortThreshold() + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
          + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableShorts) / t.getTradePercentages().size())) + " | L:" + df.format(t.getLowest())
          + " | pS:" + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void adxLongOnly() {
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);
      t = adxStrat.adxOnlyLongTradesConf(t.getKijun(), this.rawData.getKijunList().get(t.getKijun()),
          t.getAdxThreshold(), t.getCloseLongThreshold(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), this.rawData.getCandleList());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }
     
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
          + " | cThr:" + t.getCloseLongThreshold() + " | pro:" + df.format(t.getProfit()) + " |conf:"
          + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:" + df.format(((profitableLongs) / t.getTradePercentages().size()))
          + " | L:" + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs);
    }
  }

  private void adxTrailing() {
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);

      float tmpMulti = t.getAtrMultiplier();
      t = adxStrat.adxTrailingTradesConf(t.getKijun(), this.rawData.getKijunList().get(t.getKijun()),
          t.getAdxThreshold(), t.getAdxMinThreshold(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), t.getAtr(),
          this.rawData.getAtrTrailingMultipliesList().get(t.getAtr()).get((int) (t.getAtrMultiplier() * 2) - 1),
          this.rawData.getCandleList(), this.rawData.getCandleList1h());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float profitableShorts = 0;
      float negativeShorts = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }
      for (int j = 0; j < t.getShortPercentages().size(); j++) {
        if (t.getShortPercentages().get(j) >= 0) {
          profitableShorts++;
        } else {
          negativeShorts++;
        }
      }
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
          + " | atr:" + t.getAtr() + " | mu:" + tmpMulti + " | pro:" + df.format(t.getProfit()) + " | conf:"
          + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
          + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
          + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void adxPortion() {
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);

      t = adxStrat.adxThreshKijunTrades(t.getKijun(), this.rawData.getKijunList().get(t.getKijun()),
          t.getAdxThreshold(), t.getAdxMinThreshold(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), this.rawData.getCandleList());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float profitableShorts = 0;
      float negativeShorts = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }
      for (int j = 0; j < t.getShortPercentages().size(); j++) {
        if (t.getShortPercentages().get(j) >= 0) {
          profitableShorts++;
        } else {
          negativeShorts++;
        }
      }
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
          + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
          + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
          + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void trailingStop() {
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      Trades t = this.bestList.get(this.bestList.size() - i);

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float profitableShorts = 0;
      float negativeShorts = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < t.getTradePercentages().size(); j++) {
        if (t.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += t.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += t.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < t.getLongPercentages().size(); j++) {
        if (t.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }
      for (int j = 0; j < t.getShortPercentages().size(); j++) {
        if (t.getShortPercentages().get(j) >= 0) {
          profitableShorts++;
        } else {
          negativeShorts++;
        }
      }
      System.out
          .println("atr" + (t.getAtr()) + " | multi:" + (t.getAtrMultiplier()) + " | pro:" + df.format(t.getProfit())
              + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size()
              + " | t%:" + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
              + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
              + profitableShorts + " | nS:" + negativeShorts);
    }
  }
}