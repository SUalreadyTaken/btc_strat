package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Canal;
import btc_strat.Model.InputModel;
import btc_strat.Model.Trades;
import btc_strat.Strats.AdxStrat;
import btc_strat.Strats.StratUtils;

public class CanalAdxThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestCanalADXList;

  public CanalAdxThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut, List<Trades> bestCanalADXList,
      int poisonPill) {
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
        InputModel apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        
        List<Trades> tmpBestList = new ArrayList<>();

        List<Float> adxList = apModel.getAdxList();

        for (int threshold = 19; threshold < 40; threshold++) {
          for (int exitThresh = 19; exitThresh < 40; exitThresh++) {
            for (int canalLookback = 7; canalLookback < apModel.getCanalLookback(); canalLookback++) {
              Canal canal = apModel.getCanalList().get(canalLookback);
              for (int adxLookback = 7; adxLookback < apModel.getAdxLookback(); adxLookback++) {
                List<Float> tmpProfitList = adxStrat.canalAdxExitProfitList(apModel.getWList(),canalLookback, adxLookback, 10, canal,
                    apModel.getWholeKijunList().get(10), threshold, exitThresh, apModel.getAdx(), adxList, apModel.getDiList(),
                    apModel.getCandleList());
                if (tmpProfitList.size() > 70) {
                  // if (tmpBestList.size() > 1 && tmpProfitList.get(tmpProfitList.size() - 1) > 5.37) {
                    float tmpConf = stratUtils.calcConf(tmpProfitList);
                    if (tmpConf > 0.85) {
                      Trades best = new Trades();
                      best.setAdxLookback(adxLookback);
                      best.setCanalLookback(canalLookback);
                      best.setAdx(apModel.getAdx());
                      best.setAdxThreshold(threshold);
                      best.setAdxMinThreshold(exitThresh);
                      best.setKijun(10);
                      best.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
                      best.setConf(tmpConf);
                      best.setCanal(canal);
                      tmpBestList.add(best);
                    }
                  }
                // }
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
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}