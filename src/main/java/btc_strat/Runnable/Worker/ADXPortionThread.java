package btc_strat.Runnable.Worker;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.InputModel;
import btc_strat.Model.Trades;
import btc_strat.Strats.AdxStrat;

public class ADXPortionThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> betsKijunADXList;

  public ADXPortionThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut,
      List<Trades> bestKijunADXList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.betsKijunADXList = bestKijunADXList;
  }

  public void run() {
    AdxStrat adxStrat = new AdxStrat();
    try {
      while (true) {
        InputModel apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        Trades bestKA = new Trades();
        float bestProfit = -999;
        
        // find inputs.. no trades 
        for (int kijunCount = 10; kijunCount < apModel.getWholeKijunList().size(); kijunCount++) {
          List<Float> adxList = apModel.getAdxList();
          for (int lowerThreshold = 13; lowerThreshold <= 15; lowerThreshold++) {
            for (int threshold = 19; threshold < 55; threshold++) {
              Trades tmpKA = adxStrat.adxThreshKijunProfit(kijunCount, apModel.getWholeKijunList().get(kijunCount),
                  threshold, lowerThreshold, apModel.getAdx(), adxList, apModel.getDiList(), apModel.getCandleList());
              // best profit
              if (tmpKA.getProfit() > bestProfit) {
              // if (tmpKA.getProfit() > bestProfit && tmpKA.getTradeCount() > 50) {
                bestProfit = tmpKA.getProfit();
                bestKA.setAdx(apModel.getAdx());
                bestKA.setAdxMinThreshold(lowerThreshold);
                bestKA.setAdxThreshold(threshold);
                bestKA.setKijun(kijunCount);
                bestKA.setProfit(tmpKA.getProfit());
              }
            }
          }
        }

        this.betsKijunADXList.add(bestKA);

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}