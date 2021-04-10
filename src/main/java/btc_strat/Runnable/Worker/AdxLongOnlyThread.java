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

public class AdxLongOnlyThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestKijunADXLongList;

  public AdxLongOnlyThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut,
      List<Trades> bestKijunADXLongList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestKijunADXLongList = bestKijunADXLongList;
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
        
        
        for (int kijunCount = 10; kijunCount < apModel.getWholeKijunList().size(); kijunCount++) {
          List<Float> adxList = apModel.getAdxList();
          for (int closeLongThreshold = 20; closeLongThreshold <= 50; closeLongThreshold++) {
            for (int threshold = 21; threshold < 55; threshold++) {
              List<Float> tmpProfitList = adxStrat.adxOnlyLongProfitList(kijunCount,
                  apModel.getWholeKijunList().get(kijunCount), threshold, closeLongThreshold, apModel.getAdx(), adxList,
                  apModel.getDiList(), apModel.getCandleList());
              float tmpConf = stratUtils.calcConf(tmpProfitList);
              if (tmpConf > 0.8 && tmpProfitList.get(tmpProfitList.size() - 1) > 4) {
                Trades best = new Trades();
                best.setAdx(apModel.getAdx());
                best.setAdxThreshold(threshold);
                best.setCloseLongThreshold(closeLongThreshold);
                best.setKijun(kijunCount);
                best.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
                best.setConf(tmpConf);
                tmpBestList.add(best);
              }
            }
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        int size = tmpBestList.size() > 10 ? 10 : tmpBestList.size();
        for (int i = 0; i < size; i++) {
          this.bestKijunADXLongList.add(tmpBestList.get(i));
        }

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

}