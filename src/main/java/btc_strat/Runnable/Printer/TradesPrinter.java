package btc_strat.Runnable.Printer;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.MRBands;
import btc_strat.Model.MrResult;
import btc_strat.Model.RawData;
import btc_strat.Model.StratEnum;
import btc_strat.Model.Trades.AdxTrades;
import btc_strat.Model.Trades.AlligatorFractalTrades;
import btc_strat.Model.Trades.CanalAdxTrades;
import btc_strat.Model.Trades.KijunAdxTrades;
import btc_strat.Model.Trades.MRBandsTrades;
import btc_strat.Model.Trades.Trades;
import btc_strat.Model.Trades.TrailingAdxTrades;
import btc_strat.Model.Trades.TrailingTrades;
import btc_strat.Strats.AdxFractalStrat;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.AlligatorStrat;
import btc_strat.Strats.MRBandsStrat;
import btc_strat.Utils.MRBandsUtil;

public class TradesPrinter implements Runnable {
  private BlockingQueue<Integer> queue;
  private List<Trades> bestList;
  private int poisonPill;
  private int howMany;
  private StratEnum strat;
  private RawData rawData;
  private List<AdxTrades> bestAdxFractalList;
  private List<AlligatorFractalTrades> bestAlligatorFractalList;
  private List<MRBandsTrades> bestMRBandsList;
  private List<CanalAdxTrades> bestCanalAdxTradesList;
  private List<KijunAdxTrades> bestKijunAdxTradesList;
  private List<TrailingAdxTrades> bestTrailingAdxTradesList;
  private List<TrailingTrades> bestTrailingTradesList;

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
    try {
      while (true) {
        int number = queue.take();
        if (number == 1) {
          this.poisonPill--;
          if (this.poisonPill == 0) {
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
              case ALLIGATORFRACTALLONG:
                alligatorFractalLong();
                break;
              case ADXFRACTAL:
                adxFractal();
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

  private void adxFractal() {
    try {
      this.bestAdxFractalList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestAdxFractalList.size() ? this.howMany : this.bestAdxFractalList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    DecimalFormat df = new DecimalFormat("#.####");
    AdxFractalStrat adxfractalStrat = new AdxFractalStrat();
    for (int i = 1; i < this.howMany + 1; i++) {
      AdxTrades t = this.bestAdxFractalList.get(this.bestAdxFractalList.size() - i);
      AdxTrades best = adxfractalStrat.adxFractalTrades(t, this.rawData.getStart(), t.getLookback(), t.getLongThresh(),
          t.getExitThresh(), this.rawData.getFractalsDefault(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), this.rawData.getCandleList());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < best.getTradePercentages().size(); j++) {
        if (best.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += best.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += best.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < best.getLongPercentages().size(); j++) {
        if (best.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }

      System.out.println("adx:" + (t.getAdx() + 1) + " | thr:" + t.getLongThresh() + " | mThr:" + t.getExitThresh()
          + " |aL: " + t.getLookback() + " | pro:" + df.format(t.getProfit()) + " | conf:" + df.format(t.getConf())
          + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size()
          + " | t%:" + df.format((profitableLongs / t.getTradePercentages().size())) + " | pL:" + profitableLongs
          + " | nL:" + negativeLongs);

    }
  }

  private void alligatorFractalLong() {
    try {
      this.bestAlligatorFractalList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestAlligatorFractalList.size() ? this.howMany
          : this.bestAlligatorFractalList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    DecimalFormat df = new DecimalFormat("#.####");
    AlligatorStrat alligatorStrat = new AlligatorStrat();
    for (int i = 1; i < this.howMany + 1; i++) {
      AlligatorFractalTrades t = this.bestAlligatorFractalList.get(this.bestAlligatorFractalList.size() - i);
      AlligatorFractalTrades best = alligatorStrat.alligatorFractalPrint(
          this.rawData.getSsmaList().get(t.getLip()).getValues().get(t.getLipShift()),
          this.rawData.getSsmaList().get(t.getTeeth()).getValues().get(t.getTeethShift()),
          this.rawData.getSsmaList().get(t.getJaw()).getValues().get(t.getJawShift()),
          this.rawData.getFractalsDefault(), this.rawData.getCandleList(), 100);
      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float avgP = 0;
      float avgN = 0;
      for (int j = 0; j < best.getTradePercentages().size(); j++) {
        if (best.getTradePercentages().get(j) >= 0) {
          profitableTrades++;
          avgP += best.getTradePercentages().get(j);
        } else {
          negativeTrades++;
          avgN += best.getTradePercentages().get(j);
        }
      }
      avgP = avgP / profitableTrades;
      avgN = avgN / negativeTrades;
      for (int j = 0; j < best.getLongPercentages().size(); j++) {
        if (best.getLongPercentages().get(j) >= 0) {
          profitableLongs++;
        } else {
          negativeLongs++;
        }
      }

      System.out.println("lips:" + t.getLip() + " | lipS:" + t.getLipShift() + " | teeth:" + t.getTeeth() + " | teethS:"
          + t.getTeethShift() + " | jaw:" + t.getJaw() + " | jawS:" + t.getJawShift() + " | pro:"
          + df.format(best.getProfit()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + best.getTradePercentages().size() + " stops: " + best.getStopsHit() + " | t%:"
          + df.format(((profitableLongs) / best.getTradePercentages().size())) + " | pL:" + profitableLongs + " | nL:"
          + negativeLongs);
    }
  }

  private void mrbandsLong() {
    try {
      this.bestMRBandsList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestMRBandsList.size() ? this.howMany : this.bestMRBandsList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    MRBandsStrat mrbStrat = new MRBandsStrat();
    MRBandsUtil mrBandsUtil = new MRBandsUtil();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      MRBandsTrades t = this.bestMRBandsList.get(this.bestMRBandsList.size() - i);
      // System.out.println("win: " + t.getWindow() + " | deg: " + t.getDegree() + " |
      // mult: " + t.getMult() + " | pro: " + t.getProfit());
      List<MrResult> mrResultList = mrBandsUtil.getMrList(this.rawData.getCandleList(), t.getWindow(), t.getDegree());
      MRBands mrb = mrBandsUtil.getBands(mrResultList, t.getMultiplier(), t.getWindow());
      // MRBands mrb, int window, List<Candlestick> candleList
      int tmpDegree = t.getDegree();
      float tmpMult = t.getMultiplier();
      t = mrbStrat.mrbLongOnlyTrade(mrb, t.getWindow(), this.rawData.getCandleList());
      t.setDegree(tmpDegree);
      t.setMultiplier(tmpMult);
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
      System.out.println("win:" + t.getWindow() + " | deg:" + t.getDegree() + " | mult:" + t.getMultiplier() + " | pro:"
          + df.format(t.getProfit()) + " | conf:" + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:"
          + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | pL:" + profitableLongs + " | nL:"
          + negativeLongs);
    }

  }

  private void canalAdx() {
    try {
      this.bestCanalAdxTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestCanalAdxTradesList.size() ? this.howMany
          : this.bestCanalAdxTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      CanalAdxTrades t = this.bestCanalAdxTradesList.get(this.bestCanalAdxTradesList.size() - i);
      t = adxStrat.canalAdxExitTrades(this.rawData.getWList(), t.getCanalLookback(), t.getLookback(), t.getCanal(),
          t.getLongThresh(), t.getExitThresh(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
          this.rawData.getDiList().get(t.getAdx()), this.rawData.getCandleList());

      float profitableTrades = 0;
      float negativeTrades = 0;
      float profitableLongs = 0;
      float negativeLongs = 0;
      float profitableShorts = 0;
      // float negativeShorts = 0;
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

      System.out.println("adx:" + (t.getAdx() + 1) + " | thr:" + t.getLongThresh() + " | mThr:" + t.getExitThresh()
          + " |aL: " + t.getLookback() + " |cL: " + (t.getCanalLookback() + 1) + " | pro:" + df.format(t.getProfit())
          + " | conf:" + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | pL:"
          + profitableLongs + " | nL:" + negativeLongs);
    }
  }

  private void adxShortOnly() {
    try {
      this.bestKijunAdxTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestKijunAdxTradesList.size() ? this.howMany
          : this.bestKijunAdxTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      KijunAdxTrades t = this.bestKijunAdxTradesList.get(this.bestKijunAdxTradesList.size() - i);

      t = adxStrat.adxOnlyShortTrades(t.getKijunLen(), this.rawData.getKijunList().get(t.getKijunLen()),
          t.getLongThresh(), t.getExitThresh(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
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
      // NEED TO print it
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
          + " | cThr:" + t.getExitThresh() + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
          + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableShorts) / t.getTradePercentages().size())) + " | L:" + df.format(t.getLowest())
          + " | pS:" + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void adxLongOnly() {
    try {
      this.bestKijunAdxTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestKijunAdxTradesList.size() ? this.howMany
          : this.bestKijunAdxTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      KijunAdxTrades t = this.bestKijunAdxTradesList.get(this.bestKijunAdxTradesList.size() - i);
      t = adxStrat.adxOnlyLongTradesConf(t.getKijunLen(), this.rawData.getKijunList().get(t.getKijunLen()),
          t.getLongThresh(), t.getExitThresh(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
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

      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
          + " | cThr:" + t.getExitThresh() + " | pro:" + df.format(t.getProfit()) + " |conf:" + df.format(t.getConf())
          + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size()
          + " | t%:" + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | L:"
          + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs);
    }
  }

  private void adxTrailing() {
    try {
      this.bestTrailingAdxTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestTrailingAdxTradesList.size() ? this.howMany
          : this.bestTrailingAdxTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      TrailingAdxTrades t = this.bestTrailingAdxTradesList.get(this.bestTrailingAdxTradesList.size() - i);

      float tmpMulti = t.getAtrMultiplier();
      t = adxStrat.adxTrailingTradesConf(t.getKijunLen(), this.rawData.getKijunList().get(t.getKijunLen()),
          t.getLongThresh(), t.getExitThresh(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
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

      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
          + " | atr:" + t.getAtr() + " | mu:" + tmpMulti + " | pro:" + df.format(t.getProfit()) + " | conf:"
          + df.format(t.getConf()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
          + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
          + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void adxPortion() {
    try {
      this.bestKijunAdxTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestKijunAdxTradesList.size() ? this.howMany
          : this.bestKijunAdxTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    AdxStrat adxStrat = new AdxStrat();
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      KijunAdxTrades t = this.bestKijunAdxTradesList.get(this.bestKijunAdxTradesList.size() - i);

      t = adxStrat.adxThreshKijunTrades(t.getKijunLen(), this.rawData.getKijunList().get(t.getKijunLen()),
          t.getLongThresh(), t.getExitThresh(), t.getAdx(), this.rawData.getAdxList().get(t.getAdx()),
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
      // NEED TO print it
      System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
          + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
          + t.getTradePercentages().size() + " | t%:"
          + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
          + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
          + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  private void trailingStop() {
    try {
      this.bestTrailingTradesList.sort(Comparator.comparing(Trades::getProfit));
      this.howMany = this.howMany < this.bestTrailingTradesList.size() ? this.howMany
          : this.bestTrailingTradesList.size() - 1;
    } catch (Exception e) {
      System.err.println("🔨 something went wrong in printer");
      e.printStackTrace();
    }
    DecimalFormat df = new DecimalFormat("#.####");
    for (int i = 1; i < this.howMany + 1; i++) {
      TrailingTrades t = this.bestTrailingTradesList.get(this.bestTrailingTradesList.size() - i);

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
      // NEED TO print it
      System.out
          .println("atr" + (t.getAtr()) + " | mutli:" + (t.getAtrMultiplier()) + " | pro:" + df.format(t.getProfit())
              + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size()
              + " | t%:" + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
              + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:"
              + profitableShorts + " | nS:" + negativeShorts);
    }
  }

  public void setBestAdxFractalList(List<AdxTrades> bestAdxFractalList) {
    this.bestAdxFractalList = bestAdxFractalList;
  }

  public List<AdxTrades> getBestAdxFractalList() {
    return this.bestAdxFractalList;
  }

  public List<AlligatorFractalTrades> getBestAlligatorFractalList() {
    return this.bestAlligatorFractalList;
  }

  public void setBestAlligatorFractalList(List<AlligatorFractalTrades> bestAlligatorFractalList) {
    this.bestAlligatorFractalList = bestAlligatorFractalList;
  }

  public List<MRBandsTrades> getBestMRBandsList() {
    return this.bestMRBandsList;
  }

  public void setBestMRBandsList(List<MRBandsTrades> bestMRBandsList) {
    this.bestMRBandsList = bestMRBandsList;
  }

  public List<CanalAdxTrades> getBestCanalAdxTradesList() {
    return this.bestCanalAdxTradesList;
  }

  public void setBestCanalAdxTradesList(List<CanalAdxTrades> bestCanalAdxTradesList) {
    this.bestCanalAdxTradesList = bestCanalAdxTradesList;
  }

  public List<TrailingAdxTrades> getBestTrailingAdxTradesList() {
    return this.bestTrailingAdxTradesList;
  }

  public void setBestTrailingAdxTradesList(List<TrailingAdxTrades> bestTrailingAdxTradesList) {
    this.bestTrailingAdxTradesList = bestTrailingAdxTradesList;
  }

  public List<TrailingTrades> getBestTrailingTradesList() {
    return this.bestTrailingTradesList;
  }

  public void setBestTrailingTradesList(List<TrailingTrades> bestTrailingTradesList) {
    this.bestTrailingTradesList = bestTrailingTradesList;
  }

  public List<KijunAdxTrades> getBestKijunAdxTradesList() {
    return this.bestKijunAdxTradesList;
  }

  public void setBestKijunAdxTradesList(List<KijunAdxTrades> bestKijunAdxTradesList) {
    this.bestKijunAdxTradesList = bestKijunAdxTradesList;
  }

}