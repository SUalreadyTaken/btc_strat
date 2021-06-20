package btc_strat.Runnable.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.Inputs.AlligatorFractalInput;
import btc_strat.Model.Trades.AlligatorFractalTrades;
import btc_strat.Model.Trades.Trades;
import btc_strat.Strats.AlligatorStrat;

public class AlligatorThread implements Runnable {
  private BlockingQueue<AlligatorFractalInput> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<AlligatorFractalTrades> bestAlligatorList;

  public AlligatorThread(BlockingQueue<AlligatorFractalInput> queue, BlockingQueue<Integer> queueOut,
      List<AlligatorFractalTrades> bestAlligatorList, int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestAlligatorList = bestAlligatorList;
  }

  public void run() {
    AlligatorStrat alligatorStrat = new AlligatorStrat();
    try {
      while (true) {
        AlligatorFractalInput in = queue.take();
        if (in.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }
        float bestProfit = 0;
        List<AlligatorFractalTrades> tmpBestList = new ArrayList<>();
        int start = 100;
        int lip = in.getLip();
        int teeth = in.getTeeth();
        int jaw = in.getJaw();
        int shift = in.getShift();
        for (int lipShift = 0; lipShift < shift; lipShift++) {
          for (int teethShift = lipShift + 1; teethShift < shift; teethShift++) {
            for (int jawShift = teethShift + 1; jawShift < shift; jawShift++) {
              List<Float> tmpProfitList = alligatorStrat.alligatorFractalProfitList(
                  in.getSsmaList().get(lip).getValues().get(lipShift),
                  in.getSsmaList().get(teeth).getValues().get(teethShift),
                  in.getSsmaList().get(jaw).getValues().get(jawShift), in.getFractalsDefault(), in.getCandleList(),
                  start);
              if (!tmpProfitList.isEmpty() && tmpProfitList.get(tmpProfitList.size() - 1) > bestProfit) {
                AlligatorFractalTrades t = new AlligatorFractalTrades();
                t.setLip(lip);
                t.setLipShift(lipShift);
                t.setTeeth(teeth);
                t.setTeethShift(teethShift);
                t.setJaw(jaw);
                t.setJawShift(jawShift);
                bestProfit = t.getProfit();
                tmpBestList.add(t);
              }
            }
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        if (!tmpBestList.isEmpty()) {
          int size = tmpBestList.size() > 10 ? 10 : tmpBestList.size();
          for (int i = 0; i < size; i++) {
            this.bestAlligatorList.add(tmpBestList.get(i));
          }
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