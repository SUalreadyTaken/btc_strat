package btc_strat.Runnable.Worker;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.KijunAdxInput;
import btc_strat.Model.Trades.KijunAdxTrades;
import btc_strat.Strats.AdxStrat;

public class ADXPortionThread implements Runnable {
  private BlockingQueue<KijunAdxInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<KijunAdxTrades> betsKijunADXList;

  public ADXPortionThread(BlockingQueue<KijunAdxInput> queue, BlockingQueue<Integer> queueOut,
      List<KijunAdxTrades> bestKijunADXList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.betsKijunADXList = bestKijunADXList;
  }

  public void run() {
    AdxStrat adxStrat = new AdxStrat();
    try {
      while (true) {
        KijunAdxInput apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        KijunAdxTrades bestKA = new KijunAdxTrades();
        float bestProfit = -999;

        for (int kijunLen = 10; kijunLen < apModel.getWholeKijunList().size(); kijunLen++) {
          List<Float> adxList = apModel.getAdxList();
          for (int lowerThreshold = 13; lowerThreshold <= 15; lowerThreshold++) {
            for (int threshold = 19; threshold < 55; threshold++) {
              KijunAdxTrades tmpKA = adxStrat.adxThreshKijunProfit(kijunLen, apModel.getWholeKijunList().get(kijunLen),
                  threshold, lowerThreshold, apModel.getAdx(), adxList, apModel.getDiList(), apModel.getCandleList());
              // best profit
              if (tmpKA.getProfit() > bestProfit) {
                // if (tmpKA.getProfit() > bestProfit && tmpKA.getTradeCount() > 50) {
                bestProfit = tmpKA.getProfit();
                bestKA.setAdx(apModel.getAdx());
                bestKA.setExitThresh(lowerThreshold);
                bestKA.setLongThresh(threshold);
                bestKA.setKijunLen(kijunLen);
                bestKA.setProfit(tmpKA.getProfit());
              }
            }
          }
        }

        this.betsKijunADXList.add(bestKA);

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