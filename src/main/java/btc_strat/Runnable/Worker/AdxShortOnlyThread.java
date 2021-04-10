package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.InputModel;
import btc_strat.Model.Trades;
import btc_strat.Strats.AdxStrat;

public class AdxShortOnlyThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestKijunADXLongList;

  public AdxShortOnlyThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut,
      List<Trades> bestKijunADXLongList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestKijunADXLongList = bestKijunADXLongList;
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
        float bestProfit = -999;
        List<Trades> tmpBestList = new ArrayList<>();

        for (int kijunCount = 10; kijunCount < apModel.getWholeKijunList().size(); kijunCount++) {
          List<Float> adxList = apModel.getAdxList();
          for (int closeShortThreshold = 20; closeShortThreshold <= 50; closeShortThreshold++) {
            for (int threshold = 21; threshold < 55; threshold++) {
              float tmpProfit = adxStrat.adxOnlyShortProfit(kijunCount, apModel.getWholeKijunList().get(kijunCount),
                  threshold, closeShortThreshold, apModel.getAdx(), adxList, apModel.getDiList(),
                  apModel.getCandleList());
              if (tmpProfit > bestProfit) {
                bestProfit = tmpProfit;
                Trades best = new Trades();
                best.setAdx(apModel.getAdx());
                best.setAdxThreshold(threshold);
                best.setCloseShortThreshold(closeShortThreshold);
                best.setKijun(kijunCount);
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
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}