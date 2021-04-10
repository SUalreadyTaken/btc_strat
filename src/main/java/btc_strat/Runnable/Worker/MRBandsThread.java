package btc_strat.Runnable.Worker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import btc_strat.Model.InputModel;
import btc_strat.Model.MRBands;
import btc_strat.Model.MrResult;
import btc_strat.Model.Trades;
import btc_strat.Strats.MRBandsStrat;
import btc_strat.Utils.MRBandsUtil;

public class MRBandsThread implements Runnable {
  private BlockingQueue<InputModel> queue;
  private BlockingQueue<Integer> queueOut;
  private final int poisonPill;
  private List<Trades> bestMRBandsList;

  public MRBandsThread(BlockingQueue<InputModel> queue, BlockingQueue<Integer> queueOut, List<Trades> bestMRBandsList,
      int poisonPill) {
    this.queue = queue;
    this.poisonPill = poisonPill;
    this.queueOut = queueOut;
    this.bestMRBandsList = bestMRBandsList;
  }

  public void run() {
    MRBandsStrat mrbStrat = new MRBandsStrat();
    MRBandsUtil mrbUtil = new MRBandsUtil();
    try {
      while (true) {
        InputModel inputModel = queue.take();
        if (inputModel.getPoisonPill() == this.poisonPill) {
          this.queueOut.put(1);
          return;
        }

        List<Trades> tmpBestList = new ArrayList<>();
        BigDecimal end = BigDecimal.valueOf(inputModel.getMult());
        for (int degree = 2; degree <= inputModel.getDegree(); degree++) {
          List<MrResult> mrResultList = mrbUtil.getMrList(inputModel.getCandleList(), inputModel.getWindow(), degree);
          for (BigDecimal mult = new BigDecimal(2.0); mult.compareTo(end) < 0; mult = mult.add(BigDecimal.valueOf(0.1))) {
            MRBands mrb = mrbUtil.getBands(mrResultList, mult.floatValue(), inputModel.getWindow());
            List<Float> tmpProfitList = mrbStrat.mrbLongOnlyProfitList(mrb, inputModel.getWindow(),
                inputModel.getCandleList());
            // float conf = calcConf(tmpProfitList);
            // TODO add conf and trade count limit
            Trades t = new Trades();
            t.setWindow(inputModel.getWindow());
            t.setDegree(degree);
            t.setMult(mult.floatValue());
            t.setProfit(tmpProfitList.get(tmpProfitList.size() - 1));
            tmpBestList.add(t);
          }
        }

        tmpBestList.sort(Comparator.comparing(Trades::getProfit));
        Collections.reverse(tmpBestList);
        int size = tmpBestList.size() > 20 ? 20 : tmpBestList.size();
        for (int i = 0; i < size; i++) {
          this.bestMRBandsList.add(tmpBestList.get(i));
        }

      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
 
}