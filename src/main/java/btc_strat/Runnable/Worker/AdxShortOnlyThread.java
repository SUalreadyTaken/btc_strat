package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.KijunAdxInput;
import btc_strat.Model.Trades.KijunAdxTrades;
import btc_strat.Model.Trades.Trades;
import btc_strat.Strats.AdxStrat;

public class AdxShortOnlyThread implements Runnable {
  private BlockingQueue<KijunAdxInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<KijunAdxTrades> bestKijunADXLongList;

  public AdxShortOnlyThread(BlockingQueue<KijunAdxInput> queue, BlockingQueue<Integer> queueOut,
      List<KijunAdxTrades> bestKijunADXLongList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestKijunADXLongList = bestKijunADXLongList;
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
        float bestProfit = -999;
        List<KijunAdxTrades> tmpBestList = new ArrayList<>();

        for (int kijunLen = 10; kijunLen < apModel.getWholeKijunList().size(); kijunLen++) {
          List<Float> adxList = apModel.getAdxList();
          for (int closeShortThreshold = 20; closeShortThreshold <= 50; closeShortThreshold++) {
            for (int threshold = 21; threshold < 55; threshold++) {
              float tmpProfit = adxStrat.adxOnlyShortProfit(kijunLen, apModel.getWholeKijunList().get(kijunLen),
                  threshold, closeShortThreshold, apModel.getAdx(), adxList, apModel.getDiList(),
                  apModel.getCandleList());
              if (tmpProfit > bestProfit) {
                bestProfit = tmpProfit;
                KijunAdxTrades best = new KijunAdxTrades();
                best.setAdx(apModel.getAdx());
                // actually short entry
                best.setLongThresh(threshold);
                best.setExitThresh(closeShortThreshold);
                best.setKijunLen(kijunLen);
                best.setProfit(tmpProfit);
                tmpBestList.add(best);
              }
            }
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        for (int i = 0; i < 10; i++) {
          this.bestKijunADXLongList.add(tmpBestList.get(i));
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