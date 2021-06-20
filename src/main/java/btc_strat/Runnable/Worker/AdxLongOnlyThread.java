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
import btc_strat.Strats.StratUtils;

public class AdxLongOnlyThread implements Runnable {
  private BlockingQueue<KijunAdxInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<KijunAdxTrades> bestKijunADXLongList;

  public AdxLongOnlyThread(BlockingQueue<KijunAdxInput> queue, BlockingQueue<Integer> queueOut,
      List<KijunAdxTrades> bestKijunADXLongList, int poisonPill) {
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
        KijunAdxInput apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        // float bestProfit = -999;
        // float bestConf = 0;
        List<KijunAdxTrades> tmpBestList = new ArrayList<>();

        for (int kijunLen = 10; kijunLen < apModel.getWholeKijunList().size(); kijunLen++) {
          List<Float> adxList = apModel.getAdxList();
          for (int closeLongThreshold = 20; closeLongThreshold <= 50; closeLongThreshold++) {
            for (int threshold = 21; threshold < 55; threshold++) {
              List<Float> tmpProfitList = adxStrat.adxOnlyLongProfitList(kijunLen,
                  apModel.getWholeKijunList().get(kijunLen), threshold, closeLongThreshold, apModel.getAdx(), adxList,
                  apModel.getDiList(), apModel.getCandleList());
              float tmpConf = stratUtils.calcConf(tmpProfitList);
              if (tmpConf > 0.8 && tmpProfitList.get(tmpProfitList.size() - 1) > 4) {
                // bestProfit = tmpProfitList.get(tmpProfitList.size() - 1);
                // bestConf = tmpConf;
                KijunAdxTrades best = new KijunAdxTrades();
                best.setAdx(apModel.getAdx());
                best.setLongThresh(threshold);
                best.setExitThresh(closeLongThreshold);
                best.setKijunLen(kijunLen);
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