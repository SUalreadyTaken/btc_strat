package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.AdxFractalInput;
import btc_strat.Model.Trades.AdxTrades;
import btc_strat.Model.Trades.Trades;
import btc_strat.Strats.AdxFractalStrat;
import btc_strat.Strats.StratUtils;

public class AdxFractalThread implements Runnable {
  private BlockingQueue<AdxFractalInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<AdxTrades> bestAdxFractalList;

  public AdxFractalThread(BlockingQueue<AdxFractalInput> queue, BlockingQueue<Integer> queueOut,
      List<AdxTrades> bestADxFractalList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestAdxFractalList = bestADxFractalList;
  }

  public void run() {
    AdxFractalStrat strat = new AdxFractalStrat();
    StratUtils stratUtils = new StratUtils();
    try {
      while (true) {
        AdxFractalInput in = queue.take();
        if (in.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        float bestProfit = in.getProfitToBeat();
        // float bestConf = 0;
        List<AdxTrades> tmpBestList = new ArrayList<>();
        for (int adxLookback = 5; adxLookback < in.getLookback(); adxLookback++) {
          for (int longThresh = 15; longThresh < in.getLongThresh(); longThresh++) {
            for (int exitThresh = 15; exitThresh < in.getExitThresh(); exitThresh++) {
              List<Float> profitList = strat.adxFractalProfitList(in.getStart(), adxLookback, longThresh, exitThresh,
                  in.getFractalsDefault(), in.getAdxList(), in.getDiList(), in.getCandleList());
              if (!profitList.isEmpty() && profitList.size() > 40
                  && profitList.get(profitList.size() - 1) > bestProfit) {
                float conf = stratUtils.calcConf(profitList);
                if (conf > 0.8) {
                  bestProfit = profitList.get(profitList.size() - 1);
                  AdxTrades t = new AdxTrades();
                  t.setAdx(in.getAdx());
                  t.setLookback(adxLookback);
                  t.setLongThresh(longThresh);
                  t.setExitThresh(exitThresh);
                  t.setProfit(bestProfit);
                  t.setConf(conf);
                  tmpBestList.add(t);
                }
              }
            }
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        int size = tmpBestList.size() > 10 ? 10 : tmpBestList.size();
        for (int i = 0; i < size; i++) {
          this.bestAdxFractalList.add(tmpBestList.get(i));
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