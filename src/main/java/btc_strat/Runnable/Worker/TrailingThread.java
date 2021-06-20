package btc_strat.Runnable.Worker;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.TrailingInput;
import btc_strat.Model.Trades.TrailingTrades;
import btc_strat.Strats.TrailingStrat;

public class TrailingThread implements Runnable {
  private BlockingQueue<TrailingInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<TrailingTrades> bestTrailingList;

  public TrailingThread(BlockingQueue<TrailingInput> queue, BlockingQueue<Integer> queueOut,
      List<TrailingTrades> bestTrailingList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestTrailingList = bestTrailingList;
  }

  public void run() {
    TrailingStrat trailingStrat = new TrailingStrat();
    try {
      while (true) {
        TrailingInput apModel = queue.take();
        if (apModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        TrailingTrades bestKA = new TrailingTrades();
        float bestProfit = -999;
        boolean found = false;

        for (int i = 1; i < apModel.getMultiplierIsLongList().size(); i++) {
          TrailingTrades t = trailingStrat.getTrailingTrades(apModel.getAtr(), apModel.getMultiplierIsLongList().get(i),
              apModel.getCandleList());
          if (apModel.getAtr() == 5 && (i + 1) / (float) 2 == 2.5) {
            float multiplier = (i + 1) / (float) 2;
            bestKA = t;
            bestKA.setAtrMultiplier(multiplier);
            bestProfit = t.getProfit();
            bestKA.setAtr(apModel.getAtr());
            found = true;
          }
          if (t.getProfit() > bestProfit && !found) {
            float multiplier = (i + 1) / (float) 2;
            bestKA = t;
            bestKA.setAtrMultiplier(multiplier);
            bestProfit = t.getProfit();
            bestKA.setAtr(apModel.getAtr());
          }
          if (((i + 1) / (float) 2) == 6.5f && apModel.getAtr() == 14) {
            System.out.println("atr 14 mutli 6.5 pro > " + t.getProfit());
          }
        }

        this.bestTrailingList.add(bestKA);

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