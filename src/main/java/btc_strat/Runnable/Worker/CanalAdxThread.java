package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Canal;
import btc_strat.Model.Inputs.CanalAdxInput;
import btc_strat.Model.Trades.CanalAdxTrades;
import btc_strat.Model.Trades.Trades;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.StratUtils;

public class CanalAdxThread implements Runnable {
  private BlockingQueue<CanalAdxInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<CanalAdxTrades> bestCanalADXList;

  public CanalAdxThread(BlockingQueue<CanalAdxInput> queue, BlockingQueue<Integer> queueOut,
      List<CanalAdxTrades> bestCanalADXList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestCanalADXList = bestCanalADXList;
  }

  public void run() {
    AdxStrat adxStrat = new AdxStrat();
    StratUtils stratUtils = new StratUtils();
    try {
      while (true) {
        CanalAdxInput apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        List<CanalAdxTrades> tmpBestList = new ArrayList<>();

        List<Float> adxList = apModel.getAdxList();

        for (int threshold = 19; threshold < 40; threshold++) {
          for (int exitThresh = 19; exitThresh < 40; exitThresh++) {
            for (int canalLookback = 7; canalLookback < apModel.getCanalLookback(); canalLookback++) {
              Canal canal = apModel.getCanalList().get(canalLookback);
              for (int adxLookback = 7; adxLookback < apModel.getLookback(); adxLookback++) {
                List<Float> tmpProfitList = adxStrat.canalAdxExitProfitList(apModel.getWilliamList(), canalLookback,
                    adxLookback, canal, threshold, exitThresh, apModel.getAdx(), adxList, apModel.getDiList(),
                    apModel.getCandleList());
                if (tmpProfitList.size() > 70) {
                  float tmpConf = stratUtils.calcConf(tmpProfitList);
                  if (tmpConf > 0.85) {
                    CanalAdxTrades best = new CanalAdxTrades();
                    best.setLookback(adxLookback);
                    best.setCanalLookback(canalLookback);
                    best.setAdx(apModel.getAdx());
                    best.setLongThresh(threshold);
                    best.setExitThresh(exitThresh);
                    best.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
                    best.setConf(tmpConf);
                    best.setCanal(canal);
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
          this.bestCanalADXList.add(tmpBestList.get(i));
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