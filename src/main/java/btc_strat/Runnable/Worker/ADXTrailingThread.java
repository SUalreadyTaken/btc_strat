package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.InputModel;
import btc_strat.Model.Trades;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.StratUtils;

public class ADXTrailingThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestAdxTrailingList;

  public ADXTrailingThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut,
      List<Trades> bestAdxTrailingList, int poisonPill) {
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
        InputModel apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        // Trades bestKA = new Trades();
        // float bestProfit = -999;
        float bestConf = 0;

        List<Trades> tmpBestList = new ArrayList<>();
        List<Float> adxList = apModel.getAdxList();
        for (int atr = 5; atr < apModel.getAtrTrailingMultipliesList().size(); atr++) {
          List<List<Float>> atrMultiplierList = apModel.getAtrTrailingMultipliesList().get(atr);
          for (int kijunCount = 10; kijunCount < apModel.getWholeKijunList().size(); kijunCount++) {
            for (int multiplier = 2; multiplier < atrMultiplierList.size(); multiplier++) {
              for (int lowerThreshold = 13; lowerThreshold <= 13; lowerThreshold++) {
                for (int threshold = 30; threshold < 55; threshold++) {
                  List<Float> tmpProfitList = adxStrat.adxTrailingProfitConf(kijunCount,
                      apModel.getWholeKijunList().get(kijunCount), threshold, lowerThreshold, apModel.getAdx(), adxList,
                      apModel.getDiList(), atr, atrMultiplierList.get(multiplier), apModel.getCandleList(),
                      apModel.getCandleList1h());
                  float tmpConf = stratUtils.calcConf(tmpProfitList);
                  if (tmpConf > bestConf && tmpProfitList.get(tmpProfitList.size() - 1) > 4) {
                    bestConf = tmpConf;
                    Trades best = new Trades();
                    best.setConf(tmpConf);
                    best.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
                    best.setAdx(apModel.getAdx());
                    best.setAdxMinThreshold(lowerThreshold);
                    best.setAdxThreshold(threshold);
                    best.setKijun(kijunCount);
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
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}