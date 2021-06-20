package btc_strat;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.opencsv.CSVReader;

import btc_strat.Model.Canal;
import btc_strat.Model.Candlestick;
import btc_strat.Model.DI;
import btc_strat.Model.Details;
import btc_strat.Model.FractalsDefault;
import btc_strat.Model.Kijun;
import btc_strat.Model.RawData;
import btc_strat.Model.Ssma;
import btc_strat.Model.StratEnum;
import btc_strat.Model.Inputs.AdxFractalInput;
import btc_strat.Model.Inputs.AlligatorFractalInput;
import btc_strat.Model.Inputs.CanalAdxInput;
import btc_strat.Model.Inputs.KijunAdxInput;
import btc_strat.Model.Inputs.MRBandsInput;
import btc_strat.Model.Inputs.TrailingAdxInput;
import btc_strat.Model.Inputs.TrailingInput;
import btc_strat.Model.Trades.AdxFractalDetailsTrades;
import btc_strat.Model.Trades.AdxTrades;
import btc_strat.Model.Trades.AlligatorFractalTrades;
import btc_strat.Model.Trades.CanalAdxDetailsTrades;
import btc_strat.Model.Trades.CanalAdxTrades;
import btc_strat.Model.Trades.KijunAdxDetailsTrades;
import btc_strat.Model.Trades.KijunAdxTrades;
import btc_strat.Model.Trades.MRBandsTrades;
import btc_strat.Model.Trades.TrailingAdxDetailsTrades;
import btc_strat.Model.Trades.TrailingAdxTrades;
import btc_strat.Model.Trades.TrailingTrades;
import btc_strat.Runnable.Printer.TradesPrinter;
import btc_strat.Runnable.Worker.ADXPortionThread;
import btc_strat.Runnable.Worker.ADXTrailingThread;
import btc_strat.Runnable.Worker.AdxFractalThread;
import btc_strat.Runnable.Worker.AdxLongOnlyThread;
import btc_strat.Runnable.Worker.AdxShortOnlyThread;
import btc_strat.Runnable.Worker.AlligatorThread;
import btc_strat.Runnable.Worker.CanalAdxThread;
import btc_strat.Runnable.Worker.MRBandsThread;
import btc_strat.Runnable.Worker.TrailingThread;
import btc_strat.Strats.AdxFractalStrat;
import btc_strat.Strats.AdxStrat;
import btc_strat.Utils.ADXutil;
import btc_strat.Utils.AlligatorUtil;
import btc_strat.Utils.AtrUtil;
import btc_strat.Utils.CanalUtil;
import btc_strat.Utils.FractalUtil;
import btc_strat.Utils.KijunUtil;
import btc_strat.Utils.MixUtil;

public final class App {

  static String csvFile1h = "csv/1h_2019-2021.csv";

  // static String csvFile = "csv/BTCUSDT_15min_2021-march.csv";
  // static String csvFile = "csv/BTCUSDT_bull.csv";
  // static String csvFile = "csv/BTCUSDT_2019_15min.csv";
  static String csvFile = "csv/BTCUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/BTCUSDT_15min_190621.csv";
  // static String csvFile = "csv/LTCUSDT_2019-2021_15min.csv";

  // static String csvFile = "csv/BTCUSDT_2h_2019-2021.csv";
  // static String csvFile = "csv/BTCUSDT_1d_2019-2021.csv";
  // static String csvFile = "csv/BTCUSDT_1h_190621.csv";
  // static String csvFile = "csv/BTCUSDT_2019-2021_1h.csv";
  // static String csvFile = "csv/BTCUSDT_4h_2019-2021.csv";
  // static String csvFile = "csv/BTCUSDT_4h_120621.csv";

  // static String csvFile = "csv/BTCUSDT-15min_downtrend.csv";

  public static void main(String[] args) {

    CSVReader reader = null;
    List<Candlestick> candleList = new ArrayList<>();
    try {
      reader = new CSVReader(new FileReader(csvFile));
      String[] line;
      while ((line = reader.readNext()) != null) {
        String time = line[0];
        // time = time.substring(0, 10) + "T00:00:00";
        time = time.substring(0, 10) + 'T' + time.substring(11);
        Candlestick e = new Candlestick(LocalDateTime.parse(time), Float.parseFloat(line[1]), Float.parseFloat(line[2]),
            Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]));
        candleList.add(e);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // alligatorFractal(candleList, 50, 50, 500, 15);
    // adxFractal(candleList, 10, 10, 51, 51, 500, 15);
    adxFractal(candleList, 40, 30, 45, 45, 500, 15);
    // printBestCanalADX(33, 24, 22, 22, 35, candleList);
    // printBestADxFractal(candleList, 18, 12, 32, 36);

  }

  private static void printBestADxFractal(List<Candlestick> candleList, int adxLen, int lookBack, int longThresh,
      int exitThresh) {
    ADXutil adx = new ADXutil();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxLen, adxList, candleList, adx);
    FractalsDefault fractals = new FractalUtil().getFractalsDefault(candleList);

    createADXList(adxLen, adxList, candleList, adx);
    AdxFractalStrat strats = new AdxFractalStrat();
    // int start, int adx, int adxLookback, int longThresh, int exitThresh,
    // FractalsDefault fractals, List<Float> adxList, DI di, List<Candlestick>
    // candleList)
    AdxFractalDetailsTrades t = strats.adxFractalDetails(adxLen * 20, adxLen, lookBack, longThresh, exitThresh,
        fractals, adxList.get(adxLen - 1), diList.get(adxLen - 1), candleList);
    System.out.println("profit > " + t.getProfit());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      System.out.println(t.getDetailsList().get(i).withAdxFractalToString());
      // System.out.println(t.getDetailsList().get(i).couldTaketoString());
      // System.out.println(t.getDetailsList().get(i).getPro());
      // System.out.println(t.getDetailsList().get(i).getOpenDate().format(formatter));
    }

    float total = 0;
    float totalNegative = 0;
    int tnc = 0;
    long totalTime = 0;
    long totalTimeNeg = 0;
    int totalUnder2 = 0;
    int totalUnder2CouldTake = 0;
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getOpenDate() != null && t.getDetailsList().get(i).getPeakeDate() != null) {
        total += t.getDetailsList().get(i).getCouldTake();
        Duration duration = Duration.between(t.getDetailsList().get(i).getOpenDate(),
            t.getDetailsList().get(i).getPeakeDate());
        totalTime += Math.abs(duration.toMinutes());
        if (t.getDetailsList().get(i).getPercentage() < 0) {
          totalNegative += t.getDetailsList().get(i).getCouldTake();
          totalTimeNeg += Math.abs(duration.toMinutes());
          tnc++;
        }
      }
    }
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getPercentage() < 2) {
        totalUnder2++;
        if (t.getDetailsList().get(i).getCouldTake() < 2) {
          totalUnder2CouldTake++;
        }
      }
    }
    System.out.println("total under 2 > " + totalUnder2);
    System.out.println("total under 2 could take > " + totalUnder2CouldTake);
    System.out.println("arg could take > " + (total / t.getDetailsList().size()));
    System.out.println("time in min > " + (totalTime / t.getDetailsList().size()));
    System.out.println("neg could take > " + (totalNegative / tnc));
    System.out.println("neg time in min > " + (totalTimeNeg / tnc));
    t.getDetailsList().sort(Comparator.comparing(Details::getCouldTake));
    System.out.println("median > " + (t.getDetailsList().get(t.getDetailsList().size() / 2)));
    System.out.println("conf> " + t.getConf());

    float profitableTrades = 0;
    float negativeTrades = 0;
    float profitableLongs = 0;
    float negativeLongs = 0;
    float avgP = 0;
    float avgN = 0;
    for (int j = 0; j < t.getTradePercentages().size(); j++) {
      if (t.getTradePercentages().get(j) >= 0) {
        profitableTrades++;
        avgP += t.getTradePercentages().get(j);
      } else {
        negativeTrades++;
        avgN += t.getTradePercentages().get(j);
      }
    }
    avgP = avgP / profitableTrades;
    avgN = avgN / negativeTrades;
    for (int j = 0; j < t.getLongPercentages().size(); j++) {
      if (t.getLongPercentages().get(j) >= 0) {
        profitableLongs++;
      } else {
        negativeLongs++;
      }
    }

    // adx:18 | thr:32 | mThr:36 |aL: 12 | pro:13,7072 | conf:0,8089 | aP:8,2188 |
    // aN:−3,0433 |
    // tC:88 | t%:0,5682 | pL:50.0 | nL:38.0

    DecimalFormat df = new DecimalFormat("#.####");
    System.out.println("adx:" + (t.getAdx()) + " | thr:" + t.getLongThresh() + " | mThr:" + t.getExitThresh() + " |aL: "
        + t.getLookback() + " | pro:" + df.format(t.getProfit()) + " | conf:" + df.format(t.getConf()) + " | aP:"
        + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format((profitableLongs / t.getTradePercentages().size())) + " | pL:" + profitableLongs + " | nL:"
        + negativeLongs);
  }

  private static void adxFractal(List<Candlestick> candleList, int adxLen, int adxLookBack, int maxLong, int maxClose,
      int printHowMany, int consumers) {
    System.out.println("--------------------------------------------------------------");
    System.out.println(csvFile + "\nAdxFractal adx:" + adxLen + ", lookBack: " + adxLookBack + " long: " + maxLong
        + " close: " + maxLong + " print: " + printHowMany + ", threads:" + consumers);
    System.out.println(java.time.LocalTime.now());

    int start = adxLen * 20;
    float profitToBeat = 1 + ((candleList.get(candleList.size() - 1).getClose() - candleList.get(start).getClose())
        / candleList.get(start).getClose());
    System.out.println("Data starting\n" + candleList.get(start) + "\nending\n" + candleList.get(candleList.size() - 1)
        + "\nprofit above " + profitToBeat + " is all good");

    List<AdxTrades> bestAdxFractalList = new ArrayList<>();
    ADXutil adx = new ADXutil();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxLen, adxList, candleList, adx);
    FractalsDefault fractals = new FractalUtil().getFractalsDefault(candleList);

    createADXList(adxLen, adxList, candleList, adx);

    BlockingQueue<AdxFractalInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setFractalsDefault(fractals);
    rd.setCandleList(candleList);
    rd.setStart(start);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestAdxFractalList(bestAdxFractalList);
    tp.setStrat(StratEnum.ADXFRACTAL);
    new Thread(tp).start();
    for (int i = 5; i < adxLen; i++) {
      try {
        AdxFractalInput im = new AdxFractalInput();
        im.setCandleList(candleList);
        im.setLookback(adxLookBack);
        im.setLongThresh(maxLong);
        im.setExitThresh(maxClose);
        im.setAdxList(adxList.get(i));
        im.setAdx(i);
        im.setDiList(diList.get(i));
        im.setPoisonPill(0);
        im.setFractalsDefault(fractals);
        im.setStart(start);
        im.setProfitToBeat(profitToBeat);
        blockingQueue.put(im);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    for (int i = 0; i < consumers; i++) {
      try {
        AdxFractalInput im = new AdxFractalInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AdxFractalThread(blockingQueue, blockingQueueOut, bestAdxFractalList, 10)).start();
    }

  }

  private static List<Ssma> generateSsmas(List<Candlestick> candleList, int length, int shift) {
    AlligatorUtil alligatorUtil = new AlligatorUtil();
    List<Ssma> result = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      result.add(alligatorUtil.getSsma(candleList, i, shift));
    }
    return result;
  }

  private static void alligatorFractal(List<Candlestick> candleList, int alligatorLength, int alligatorShift,
      int printHowMany, int consumers) {
    System.out.println("--------------------------------------------------------------");
    System.out.println(csvFile + "\nAlligatorFractal len:" + alligatorLength + ", shift: " + alligatorShift
        + ", threads:" + consumers);
    System.out.println(java.time.LocalTime.now());
    List<Ssma> ssmaList = generateSsmas(candleList, alligatorLength, alligatorShift);
    FractalUtil fractalUtil = new FractalUtil();
    FractalsDefault fractals = fractalUtil.getFractalsDefault(candleList);

    BlockingQueue<AlligatorFractalInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    List<AlligatorFractalTrades> alligatorFractalTrades = new ArrayList<>();
    RawData rd = new RawData();
    rd.setSsmaList(ssmaList);
    rd.setFractalsDefault(fractals);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setStrat(StratEnum.ALLIGATORFRACTALLONG);
    tp.setBestAlligatorFractalList(alligatorFractalTrades);
    new Thread(tp).start();
    for (int lip = 4; lip < alligatorLength; lip++) {
      for (int teeth = lip + 1; teeth < alligatorLength; teeth++) {
        for (int jaw = teeth + 1; jaw < alligatorLength; jaw++) {
          try {
            AlligatorFractalInput im = new AlligatorFractalInput();
            im.setShift(alligatorShift);
            im.setLip(lip);
            im.setTeeth(teeth);
            im.setJaw(jaw);
            im.setFractalsDefault(fractals);
            im.setSsmaList(ssmaList);
            im.setCandleList(candleList);
            im.setPoisonPill(0);
            blockingQueue.put(im);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        AlligatorFractalInput im = new AlligatorFractalInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AlligatorThread(blockingQueue, blockingQueueOut, alligatorFractalTrades, 10)).start();
    }
  }

  private static void bestMRBands(List<Candlestick> candleList, int window, int degree, float mult, int printHowMany,
      int consumers) {
    System.out.println("--------------------------------------------------------------");
    System.out.println(
        csvFile + "\nbestMRBands win:" + window + ", deg: " + degree + " , mult: " + mult + ", threads:" + consumers);
    List<MRBandsTrades> bestMRBandsTrades = new ArrayList<>();
    BlockingQueue<MRBandsInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestMRBandsList(bestMRBandsTrades);
    tp.setStrat(StratEnum.MRBANDSLONG);
    new Thread(tp).start();

    for (int i = 100; i < window; i++) {
      try {
        MRBandsInput im = new MRBandsInput();
        im.setWindow(i);
        im.setDegree(degree);
        im.setMultiplier(mult);
        im.setCandleList(candleList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        MRBandsInput im = new MRBandsInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new MRBandsThread(blockingQueue, blockingQueueOut, bestMRBandsTrades, 10)).start();
    }
  }

  private static void printBestCanalADX(int adxCount, int goLong, int closeLong, int adxLookback, int canalLookback,
      List<Candlestick> candleList) {
    CanalUtil cUtil = new CanalUtil();
    List<Canal> canalList = createCanalList(canalLookback, candleList, cUtil);
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createADXList(adxCount, adxList, candleList, adx);
    AdxStrat adxStrat = new AdxStrat();
    List<Float> wList = new MixUtil().getWilliamList100(14, candleList);
    // Trades t = adxStrat.adxOnlyLongDetails((kijunCount - 1),
    // kijunList.get((kijunCount - 1)), goLong, closeLong,
    // (adxCount - 1), adxList.get(adxCount - 1), diList.get(adxCount - 1),
    // candleList);

    CanalAdxDetailsTrades t = adxStrat.canalAdxExitDetails(wList, (canalLookback - 1), adxLookback,
        canalList.get(canalLookback - 1), goLong, closeLong, adxCount - 1, adxList.get(adxCount - 1),
        diList.get(adxCount - 1), candleList);
    System.out.println("profit > " + t.getProfit());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (int i = 0; i < t.getDetailsList().size(); i++) {
      System.out.println(t.getDetailsList().get(i).couldTaketoString());
      // System.out.println(t.getDetailsList().get(i).getPro());
      // System.out.println(t.getDetailsList().get(i).getOpenDate().format(formatter));
    }

    float total = 0;
    float totalNegative = 0;
    int tnc = 0;
    long totalTime = 0;
    long totalTimeNeg = 0;
    int totalUnder2 = 0;
    int totalUnder2CouldTake = 0;
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getOpenDate() != null && t.getDetailsList().get(i).getPeakeDate() != null) {
        total += t.getDetailsList().get(i).getCouldTake();
        Duration duration = Duration.between(t.getDetailsList().get(i).getOpenDate(),
            t.getDetailsList().get(i).getPeakeDate());
        totalTime += Math.abs(duration.toMinutes());
        if (t.getDetailsList().get(i).getPercentage() < 0) {
          totalNegative += t.getDetailsList().get(i).getCouldTake();
          totalTimeNeg += Math.abs(duration.toMinutes());
          tnc++;
        }
      }
    }
    float pro = 1;
    float over37 = 0;
    float under37 = 0;
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getCouldTake() > 3.7) {
        pro = (float) 1.037 * pro;
        over37++;
      } else {
        pro = ((float) (1 + (t.getDetailsList().get(i).getPercentage() / 100))) * pro;
        under37++;
      }
    }
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getPercentage() < 2) {
        totalUnder2++;
        if (t.getDetailsList().get(i).getCouldTake() < 2) {
          totalUnder2CouldTake++;
        }
      }
    }
    System.out.println("pro > " + pro + " | % " + (over37 / (over37 + under37)));
    System.out.println("total under 2 > " + totalUnder2);
    System.out.println("total under 2 could take > " + totalUnder2CouldTake);
    System.out.println("arg could take > " + (total / t.getDetailsList().size()));
    System.out.println("time in min > " + (totalTime / t.getDetailsList().size()));
    System.out.println("neg could take > " + (totalNegative / tnc));
    System.out.println("neg time in min > " + (totalTimeNeg / tnc));
    t.getDetailsList().sort(Comparator.comparing(Details::getCouldTake));
    System.out.println("median > " + (t.getDetailsList().get(t.getDetailsList().size() / 2)));
    System.out.println("conf> " + t.getConf());

    float profitableTrades = 0;
    float negativeTrades = 0;
    float profitableLongs = 0;
    float negativeLongs = 0;
    float avgP = 0;
    float avgN = 0;
    for (int j = 0; j < t.getTradePercentages().size(); j++) {
      if (t.getTradePercentages().get(j) >= 0) {
        profitableTrades++;
        avgP += t.getTradePercentages().get(j);
      } else {
        negativeTrades++;
        avgN += t.getTradePercentages().get(j);
      }
    }
    avgP = avgP / profitableTrades;
    avgN = avgN / negativeTrades;
    for (int j = 0; j < t.getLongPercentages().size(); j++) {
      if (t.getLongPercentages().get(j) >= 0) {
        profitableLongs++;
      } else {
        negativeLongs++;
      }
    }
    // NEED TO print it
    DecimalFormat df = new DecimalFormat("#.####");
    System.out.println("adx:" + (t.getAdx() + 1) + " | thr:" + t.getLongThresh() + " | cThr:" + t.getExitThresh()
        + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP) + " | aN:" + df.format(avgN) + " | tC:"
        + t.getTradePercentages().size() + " | t%:" + df.format(((profitableLongs) / t.getTradePercentages().size()))
        + " | L:" + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs);

  }

  private static void bestCanalADX(int adxLookback, int canalLookback, int adxCount, int kijunCount, int consumers,
      int printHowMany, List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestCanalADX(" + adxLookback + ", " + canalLookback + ", " + adxCount + ", "
        + kijunCount + ", " + consumers + ")");

    List<CanalAdxTrades> bestCanalAdxTrades = new ArrayList<>();
    CanalUtil cUtil = new CanalUtil();
    List<Canal> canalList = createCanalList(canalLookback, candleList, cUtil);
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    List<Float> wList = new MixUtil().getWilliamList100(14, candleList);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<CanalAdxInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setWList(wList);
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestCanalAdxTradesList(bestCanalAdxTrades);
    tp.setStrat(StratEnum.CANALADX);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        CanalAdxInput im = new CanalAdxInput();
        im.setLookback(adxLookback);
        im.setCanalList(canalList);
        im.setCanalLookback(canalLookback);
        im.setAdx(i);
        im.setCandleList(candleList);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setPoisonPill(0);
        im.setWilliamList(wList);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        CanalAdxInput im = new CanalAdxInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new CanalAdxThread(blockingQueue, blockingQueueOut, bestCanalAdxTrades, 10)).start();
    }

  }

  private static void printBestADxLongOnly(int adxCount, int goLong, int closeLong, int kijunCount,
      List<Candlestick> candleList) {
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(30, adxList, candleList, adx);
    createKijunList(75, kijunList, candleList, kijun);
    createADXList(30, adxList, candleList, adx);
    AdxStrat adxStrat = new AdxStrat();

    // (int kijunCount, Kijun kijun, int threshold, int closeLongThreshold, int
    // adxCount,
    // List<Float> adxList, DI di, List<Candlestick> candleList) {
    KijunAdxDetailsTrades t = adxStrat.adxOnlyLongDetails((kijunCount - 1), kijunList.get((kijunCount - 1)), goLong,
        closeLong, (adxCount - 1), adxList.get(adxCount - 1), diList.get(adxCount - 1), candleList);
    System.out.println("profit > " + t.getProfit());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (int i = 0; i < t.getDetailsList().size(); i++) {
      System.out.println(t.getDetailsList().get(i).couldTaketoString());
      // System.out.println(t.getDetailsList().get(i).getPro());
      // System.out.println(t.getDetailsList().get(i).getOpenDate().format(formatter));
    }

    float total = 0;
    float totalNegative = 0;
    int tnc = 0;
    long totalTime = 0;
    long totalTimeNeg = 0;
    int totalUnder2 = 0;
    int totalUnder2CouldTake = 0;
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      total += t.getDetailsList().get(i).getCouldTake();
      Duration duration = Duration.between(t.getDetailsList().get(i).getOpenDate(),
          t.getDetailsList().get(i).getPeakeDate());
      totalTime += Math.abs(duration.toMinutes());
      if (t.getDetailsList().get(i).getPercentage() < 0) {
        totalNegative += t.getDetailsList().get(i).getCouldTake();
        totalTimeNeg += Math.abs(duration.toMinutes());
        tnc++;
      }
    }
    for (int i = 0; i < t.getDetailsList().size(); i++) {
      if (t.getDetailsList().get(i).getPercentage() < 2) {
        totalUnder2++;
        if (t.getDetailsList().get(i).getCouldTake() < 2) {
          totalUnder2CouldTake++;
        }
      }
    }
    System.out.println("total under 2 > " + totalUnder2);
    System.out.println("total under 2 could take > " + totalUnder2CouldTake);
    System.out.println("arg could take > " + (total / t.getDetailsList().size()));
    System.out.println("time in min > " + (totalTime / t.getDetailsList().size()));
    System.out.println("neg could take > " + (totalNegative / tnc));
    System.out.println("neg time in min > " + (totalTimeNeg / tnc));
    t.getDetailsList().sort(Comparator.comparing(Details::getCouldTake));
    System.out.println("median > " + (t.getDetailsList().get(t.getDetailsList().size() / 2)));
    System.out.println("conf> " + t.getConf());

    float profitableTrades = 0;
    float negativeTrades = 0;
    float profitableLongs = 0;
    float negativeLongs = 0;
    float avgP = 0;
    float avgN = 0;
    for (int j = 0; j < t.getTradePercentages().size(); j++) {
      if (t.getTradePercentages().get(j) >= 0) {
        profitableTrades++;
        avgP += t.getTradePercentages().get(j);
      } else {
        negativeTrades++;
        avgN += t.getTradePercentages().get(j);
      }
    }
    avgP = avgP / profitableTrades;
    avgN = avgN / negativeTrades;
    for (int j = 0; j < t.getLongPercentages().size(); j++) {
      if (t.getLongPercentages().get(j) >= 0) {
        profitableLongs++;
      } else {
        negativeLongs++;
      }
    }
    // NEED TO print it
    DecimalFormat df = new DecimalFormat("#.####");
    System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
        + " | cThr:" + t.getExitThresh() + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP) + " | aN:"
        + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | L:" + df.format(t.getLowest())
        + " | pL:" + profitableLongs + " | nL:" + negativeLongs);

  }

  /**
   * Printer will print 10 best from each adx
   */
  private static void bestAdxLongOnly(int adxCount, int kijunCount, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestAdxLongOnly(" + adxCount + ", " + kijunCount + ", " + consumers + ")");

    List<KijunAdxTrades> bestKijunAdxLongOnlyTrades = new ArrayList<>();

    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<KijunAdxInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestKijunAdxTradesList(bestKijunAdxLongOnlyTrades);
    tp.setStrat(StratEnum.ADXLONGONLY);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setAdx(i);
        im.setCandleList(candleList);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setWholeKijunList(kijunList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AdxLongOnlyThread(blockingQueue, blockingQueueOut, bestKijunAdxLongOnlyTrades, 10)).start();
    }

  }

  /**
   * Printer will print 10 best from each adx
   */
  private static void bestAdxShortOnly(int adxCount, int kijunCount, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestAdxShortOnly(" + adxCount + ", " + kijunCount + ", " + consumers + ")");

    List<KijunAdxTrades> bestKijunAdxShortOnlyTrades = new ArrayList<>();

    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<KijunAdxInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestKijunAdxTradesList(bestKijunAdxShortOnlyTrades);
    tp.setStrat(StratEnum.ADXSHORTONLY);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setAdx(i);
        im.setCandleList(candleList);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setWholeKijunList(kijunList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AdxShortOnlyThread(blockingQueue, blockingQueueOut, bestKijunAdxShortOnlyTrades, 10)).start();
    }

  }

  private static void printBestAdxTrailing(int adxCount, int kijunCount, int highTre, int lowTre, int atrCount,
      float multi, List<Candlestick> candleList, List<Candlestick> candleList1h) {
    AtrUtil atr = new AtrUtil();
    List<List<List<Float>>> atrTrailingMultipliesList = createAtrMultiplierAtrTrailingList(30, 7, candleList1h, atr);
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(30, adxList, candleList, adx);
    createKijunList(75, kijunList, candleList, kijun);
    createADXList(30, adxList, candleList, adx);
    AdxStrat adxStrat = new AdxStrat();

    // Trades t = adxStrat.adxTrailingTradesDetails(48, kijunList.get(48), 54, 15,
    // 6, adxList.get(6), diList.get(6), 5,
    // atrTrailingMultipliesList.get(5).get(12), candleList, candleList1h);
    TrailingAdxDetailsTrades t = adxStrat.adxTrailingTradesDetails((kijunCount - 1), kijunList.get((kijunCount - 1)),
        highTre, lowTre, (adxCount - 1), adxList.get(adxCount - 1), diList.get(adxCount - 1), atrCount,
        atrTrailingMultipliesList.get(atrCount).get((int) (multi * 2) - 1), candleList, candleList1h);
    System.out.println("profit > " + t.getProfit());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (int i = 0; i < t.getDetailsList().size(); i++) {
      System.out.println(t.getDetailsList().get(i).couldTaketoString());
      // System.out.println(t.getDetailsList().get(i).getPro());
      // System.out.println(t.getDetailsList().get(i).getOpenDate().format(formatter));
    }
    float profitableTrades = 0;
    float negativeTrades = 0;
    float profitableLongs = 0;
    float negativeLongs = 0;
    float profitableShorts = 0;
    float negativeShorts = 0;
    float avgP = 0;
    float avgN = 0;
    for (int j = 0; j < t.getTradePercentages().size(); j++) {
      if (t.getTradePercentages().get(j) >= 0) {
        profitableTrades++;
        avgP += t.getTradePercentages().get(j);
      } else {
        negativeTrades++;
        avgN += t.getTradePercentages().get(j);
      }
    }
    avgP = avgP / profitableTrades;
    avgN = avgN / negativeTrades;
    for (int j = 0; j < t.getLongPercentages().size(); j++) {
      if (t.getLongPercentages().get(j) >= 0) {
        profitableLongs++;
      } else {
        negativeLongs++;
      }
    }
    for (int j = 0; j < t.getShortPercentages().size(); j++) {
      if (t.getShortPercentages().get(j) >= 0) {
        profitableShorts++;
      } else {
        negativeShorts++;
      }
    }
    // NEED TO print it
    DecimalFormat df = new DecimalFormat("#.####");
    System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijunLen() + 1) + " | thr:" + t.getLongThresh()
        + " | atr:" + t.getAtr() + " | mu:" + multi + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
        + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
        + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:" + profitableShorts
        + " | nS:" + negativeShorts);
    System.out.println("negativeSize > " + t.getNegativeDetails().size());
    // for (int i = 0; i < t.getTop5Negative().size(); i++) {
    // System.out.println("negative > " + t.getTop5Negative().get(i).toString());
    // }
  }

  private static void bestAdxTrailing(int adxCount, int kijunCount, int atrCount, float maxMultiplier, int consumers,
      int printHowMany, List<Candlestick> candleList, List<Candlestick> candleList1h) {
    System.out.println(csvFile + "\nbestAdxTrailing(" + adxCount + ", " + kijunCount + ", " + atrCount + ", "
        + maxMultiplier + ", " + consumers + ")");

    AtrUtil atr = new AtrUtil();
    List<TrailingAdxTrades> bestTrailingAdxTrades = new ArrayList<>();
    List<List<List<Float>>> atrTrailingMultipliesList = createAtrMultiplierAtrTrailingList(atrCount, maxMultiplier,
        candleList1h, atr);
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<TrailingAdxInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAtrTrailingMultipliesList(atrTrailingMultipliesList);
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    rd.setCandleList1h(candleList1h);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setRawData(rd);
    tp.setBestTrailingAdxTradesList(bestTrailingAdxTrades);
    tp.setStrat(StratEnum.ADXTRAILING);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        TrailingAdxInput im = new TrailingAdxInput();
        im.setAdx(i);
        im.setCandleList(candleList);
        im.setCandleList1h(candleList1h);
        im.setAtrTrailingMultipliesList(atrTrailingMultipliesList);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setWholeKijunList(kijunList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        TrailingAdxInput im = new TrailingAdxInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new ADXTrailingThread(blockingQueue, blockingQueueOut, bestTrailingAdxTrades, 10)).start();
    }

  }

  private static void bestTrailingStop(int atrCount, float maxMultiplier, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestTrailingStop(" + atrCount + ", " + maxMultiplier + ", " + consumers + ")");
    AtrUtil atr = new AtrUtil();
    List<TrailingTrades> bestTrailingTrades = new ArrayList<>();
    List<List<List<Boolean>>> atrMultipliesIsLongList = createAtrMultiplierIsLongList(atrCount, maxMultiplier,
        candleList, atr);

    BlockingQueue<TrailingInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setBestTrailingTradesList(bestTrailingTrades);
    tp.setStrat(StratEnum.TRAILINGSTOP);
    new Thread(tp).start();

    for (int i = 5; i < atrCount; i++) {
      try {
        TrailingInput im = new TrailingInput();
        im.setAtr(i);
        im.setCandleList(candleList);
        im.setMultiplierIsLongList(atrMultipliesIsLongList.get(i));
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        TrailingInput im = new TrailingInput();
        // im.setAdx(0);
        // im.setAdxList(null);
        // im.setDiList(null);
        // im.setWholeKijunList(null);
        // im.setCandleList(null);
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new TrailingThread(blockingQueue, blockingQueueOut, bestTrailingTrades, 10)).start();
    }
  }

  private static void bestADXPortion(int adxCount, int kijunCount, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestADXPortion(" + adxCount + ", " + kijunCount + ", " + consumers + ")");
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<KijunAdxTrades> bestAdxPortionList = new ArrayList<>();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<KijunAdxInput> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setBestKijunAdxTradesList(bestAdxPortionList);
    tp.setRawData(rd);
    tp.setStrat(StratEnum.ADXPORTION);
    new Thread(tp).start();

    for (int i = 8; i < adxCount; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setAdx(i);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setWholeKijunList(kijunList);
        im.setCandleList(candleList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        KijunAdxInput im = new KijunAdxInput();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new ADXPortionThread(blockingQueue, blockingQueueOut, bestAdxPortionList, 10)).start();
    }
  }

  private static void createKijunList(int dayCount, List<Kijun> kijunList, List<Candlestick> candleList,
      KijunUtil kijun) {
    for (int i = 1; i < dayCount + 1; i++) {
      Kijun tmpKijun = kijun.calculateKijun(candleList, i);
      kijunList.add(tmpKijun);
    }
  }

  private static void createADXList(int dayCount, List<List<Float>> adxList, List<Candlestick> candleList,
      ADXutil adx) {
    for (int i = 1; i < dayCount + 1; i++) {
      List<Float> tmpADX = adx.getAdx(i, candleList);
      adxList.add(tmpADX);
    }
  }

  private static List<DI> createDIList(int adxCount, List<List<Float>> adxList, List<Candlestick> candleList,
      ADXutil adx) {
    List<DI> result = new ArrayList<>();
    for (int i = 1; i < adxCount + 1; i++) {
      result.add(adx.getDI(i, candleList));
    }
    return result;

  }

  private static List<List<List<Boolean>>> createAtrMultiplierIsLongList(int atrCount, float multiplier,
      List<Candlestick> candleList, AtrUtil atr) {
    List<List<List<Boolean>>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int i = 1; i < atrCount; i++) {
      List<List<Boolean>> multiplierList = new ArrayList<>();
      for (float j = 0.5f; j < multiplier; j += 0.5f) {
        multiplierList.add(atr.getAtrIsLong(i, j, candleList));
      }
      result.add(multiplierList);
    }
    return result;

  }

  private static List<List<List<Float>>> createAtrMultiplierAtrTrailingList(int atrCount, float multiplier,
      List<Candlestick> candleList, AtrUtil atr) {
    List<List<List<Float>>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int i = 1; i < atrCount; i++) {
      List<List<Float>> multiplierList = new ArrayList<>();
      for (float j = 0.5f; j < multiplier; j += 0.5f) {
        multiplierList.add(atr.getAtr(i, j, candleList));
      }
      result.add(multiplierList);
    }
    return result;

  }

  private static List<Canal> createCanalList(int canalCount, List<Candlestick> candlestickList, CanalUtil cUtil) {
    List<Canal> result = new ArrayList<>();
    result.add(new Canal(null, null));
    for (int i = 1; i < canalCount + 1; i++) {
      result.add(cUtil.getCanals(candlestickList, i));
    }
    return result;
  }
}
