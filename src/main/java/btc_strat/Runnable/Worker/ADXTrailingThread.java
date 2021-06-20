package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.TrailingAdxInput;
import btc_strat.Model.Trades.Trades;
import btc_strat.Model.Trades.TrailingAdxTrades;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.StratUtils;

public class ADXTrailingThread implements Runnable {
  private BlockingQueue<TrailingAdxInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<TrailingAdxTrades> bestAdxTrailingList;

  public ADXTrailingThread(BlockingQueue<TrailingAdxInput> queue, BlockingQueue<Integer> queueOut,
      List<TrailingAdxTrades> bestAdxTrailingList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestAdxTrailingList = bestAdxTrailingList;
  }

  public void run() {
    AdxStrat adxStrat = new AdxStrat();
    StratUtils stratUtils = new StratUtils();
    try {
      while (true) {
        TrailingAdxInput apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        float bestConf = 0;

        List<TrailingAdxTrades> tmpBestList = new ArrayList<>();
        List<Float> adxList = apModel.getAdxList();
        for (int atr = 5; atr < apModel.getAtrTrailingMultipliesList().size(); atr++) {
          List<List<Float>> atrMultiplierList = apModel.getAtrTrailingMultipliesList().get(atr);
          for (int kijunLen = 10; kijunLen < apModel.getWholeKijunList().size(); kijunLen++) {
            for (int multiplier = 2; multiplier < atrMultiplierList.size(); multiplier++) {
              for (int lowerThreshold = 13; lowerThreshold <= 13; lowerThreshold++) {
                for (int threshold = 30; threshold < 55; threshold++) {
                  List<Float> tmpProfitList = adxStrat.adxTrailingProfitConf(kijunLen,
                      apModel.getWholeKijunList().get(kijunLen), threshold, lowerThreshold, apModel.getAdx(), adxList,
                      apModel.getDiList(), atr, atrMultiplierList.get(multiplier), apModel.getCandleList(),
                      apModel.getCandleList1h());
                  float tmpConf = stratUtils.calcConf(tmpProfitList);
                  if (tmpConf > bestConf && tmpProfitList.get(tmpProfitList.size() - 1) > 4) {
                    bestConf = tmpConf;
                    TrailingAdxTrades best = new TrailingAdxTrades();
                    best.setConf(tmpConf);
                    best.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
                    best.setAdx(apModel.getAdx());
                    best.setExitThresh(lowerThreshold);
                    best.setLongThresh(threshold);
                    best.setKijunLen(kijunLen);
                    best.setAtr(atr);
                    best.setAtrMultiplier((multiplier + 1) / (float) 2);
                    tmpBestList.add(best);
                  }
                }
              }
            }
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        int size = tmpBestList.size() > 10 ? 10 : tmpBestList.size();
        for (int i = 0; i < size; i++) {
          this.bestAdxTrailingList.add(tmpBestList.get(i));
        }
      }
    } catch (Exception e) {
      System.err.println("🧨 got error in thread put 1 in queue.. will miss a lot of samples");
      try {
        this.queueOut.put(1);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      Thread.currentThread().interrupt();
    }
  }
}