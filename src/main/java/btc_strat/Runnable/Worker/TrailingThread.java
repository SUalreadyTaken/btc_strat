package btc_strat.Runnable.Worker;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.InputModel;
import btc_strat.Model.Trades;
import btc_strat.Strats.TrailingStrat;

public class TrailingThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestTrailingList;

  public TrailingThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut,
      List<Trades> bestTrailingList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestTrailingList = bestTrailingList;
  }

  public void run() {
    TrailingStrat trailingStrat = new TrailingStrat();
    try {
      while (true) {
        InputModel apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        Trades bestKA = new Trades();
        float bestProfit = -999;
        boolean found = false;
        
        for(int i = 1; i < apModel.getMultiplierIsLongList().size(); i++) {
          Trades t = trailingStrat.getTrailingTrades(apModel.getAtr(), apModel.getMultiplierIsLongList().get(i), apModel.getCandleList());
          if (apModel.getAtr() == 5 && (i + 1) / (float) 2 == 2.5) {
            float multiplier = (i + 1) / (float) 2;
            bestKA = t;
            bestKA.setAtrMultiplier(multiplier);
            bestProfit =  t.getProfit();
            bestKA.setAtr(apModel.getAtr());
            found = true;
          }
          if (t.getProfit() > bestProfit && !found) {
            float multiplier = (i + 1) / (float) 2;
            bestKA = t;
            bestKA.setAtrMultiplier(multiplier);
            bestProfit =  t.getProfit();
            bestKA.setAtr(apModel.getAtr());
          }
        }

        this.bestTrailingList.add(bestKA);

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}