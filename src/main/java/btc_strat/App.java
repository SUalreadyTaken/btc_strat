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
import btc_strat.Model.InputModel;
import btc_strat.Model.Kijun;
import btc_strat.Model.RawData;
import btc_strat.Model.StratEnum;
import btc_strat.Model.Trades;
import btc_strat.Runnable.Printer.TradesPrinter;
import btc_strat.Runnable.Worker.ADXPortionThread;
import btc_strat.Runnable.Worker.ADXTrailingThread;
import btc_strat.Runnable.Worker.AdxLongOnlyThread;
import btc_strat.Runnable.Worker.AdxShortOnlyThread;
import btc_strat.Runnable.Worker.CanalAdxThread;
import btc_strat.Runnable.Worker.MRBandsThread;
import btc_strat.Runnable.Worker.TrailingThread;
import btc_strat.Strats.AdxStrat;
import btc_strat.Utils.ADXutil;
import btc_strat.Utils.AtrUtil;
import btc_strat.Utils.CanalUtil;
import btc_strat.Utils.KijunUtil;
import btc_strat.Utils.MixUtil;

public final class App {
  

  static String csvFile = "csv/BTCUSDT_15min_2021-march.csv";
  // static String csvFile = "csv/BTCUSDT_bull.csv";
  // static String csvFile = "csv/BTCUSDT_2019_15min.csv";
  // static String csvFile = "csv/BTCUSDT_2019-2021_15min_cut.csv";

  // static String csvFile = "csv/BTCUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/BTCUSDT_2019-2021_1h.csv";
  // static String csvFile1h = "csv/BTCUSDT_2019-2021_1h.csv";

  // static String csvFile = "csv/ETHUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/ETHUSDT_2019-2021_1h.csv";
  // static String csvFile1h = "csv/ETHUSDT_2019-2021_1h.csv";

  // static String csvFile = "csv/LTCUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/LTCUSDT_2019-2021_1h.csv";
  // static String csvFile1h = "csv/LTCUSDT_2019-2021_1h.csv";

  // static String csvFile = "csv/EOSUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/EOSUSDT_2019-2021_1h.csv";
  // static String csvFile1h = "csv/EOSUSDT_2019-2021_1h.csv";

  // static String csvFile = "csv/LINKUSDT_2019-2021_15min.csv";
  // static String csvFile = "csv/LINKUSDT_2019-2021_1h.csv";
  // static String csvFile1h = "csv/LINKUSDT_2019-2021_1h.csv";

  // static String csvFile = "csv/BTCUSDT-15min_downtrend.csv";

  public static void main(String[] args) {

    CSVReader reader = null;
    List<Candlestick> candleList = new ArrayList<>();
    try {
      reader = new CSVReader(new FileReader(csvFile));
      String[] line;
      while ((line = reader.readNext()) != null) {
        String time = line[0];
        time = time.substring(0, 10) + 'T' + time.substring(11);
        Candlestick e = new Candlestick(LocalDateTime.parse(time), Float.parseFloat(line[1]), Float.parseFloat(line[2]),
            Float.parseFloat(line[3]), Float.parseFloat(line[4]), Float.parseFloat(line[5]));
        candleList.add(e);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // List<Candlestick> candleList1h = new ArrayList<>();
    // try {
    // reader = new CSVReader(new FileReader(csvFile1h));
    // String[] line;
    // while ((line = reader.readNext()) != null) {
    // String time = line[0];
    // time = time.substring(0, 10) + 'T' + time.substring(11);
    // Candlestick e = new Candlestick(LocalDateTime.parse(time),
    // Float.parseFloat(line[1]), Float.parseFloat(line[2]),
    // Float.parseFloat(line[3]), Float.parseFloat(line[4]),
    // Float.parseFloat(line[5]));
    // candleList1h.add(e);
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // System.exit(1);
    // }

    bestCanalADX(40, 40, 40, 70, 12, 300, candleList);

    // bestMRBands(candleList, 100, 3, 3.5f, 50, 10);
    // bestMRBands(candleList, 300, 3, 5.0f, 150, 12);
    // bestMRBands(candleList, 300, 5, 5.0f, 100, 12);

  }

  private static void bestMRBands(List<Candlestick> candleList, int window, int degree, float mult, int printHowMany, int consumers) {
    System.out.println("--------------------------------------------------------------");
    System.out.println(csvFile + "\nbestMRBands win:" + window + ", deg: " + degree + " , mult: " + mult + ", threads:" + consumers);
    List<Trades> bestMRBandsTrades = new ArrayList<>();
    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    RawData rd = new RawData();
    rd.setCandleList(candleList);
    tp.setRawData(rd);
    tp.setBestList(bestMRBandsTrades);
    tp.setStrat(StratEnum.MRBANDSLONG);
    new Thread(tp).start();

    for (int i = 100; i < window; i++) {
      try {
        InputModel im = new InputModel();
        im.setWindow(i);
        im.setDegree(degree);
        im.setMult(mult);
        im.setCandleList(candleList);
        im.setPoisonPill(0);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        InputModel im = new InputModel();
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

    Trades t = adxStrat.canalAdxExitDetails(wList, (canalLookback - 1), adxLookback, 10, canalList.get(canalLookback - 1),
        new Kijun(null, null), goLong, closeLong, adxCount - 1, adxList.get(adxCount - 1), diList.get(adxCount - 1),
        candleList);
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
    System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
        + " | cThr:" + t.getCloseLongThreshold() + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
        + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | L:" + df.format(t.getLowest())
        + " | pL:" + profitableLongs + " | nL:" + negativeLongs);

  }

  private static void bestCanalADX(int adxLookback, int canalLookback, int adxCount, int kijunCount, int consumers,
      int printHowMany, List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestCanalADX(" + adxLookback + ", " + canalLookback + ", " + adxCount + ", "
        + kijunCount + ", " + consumers + ")");

    List<Trades> bestTrailingTrades = new ArrayList<>();
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

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    RawData rd = new RawData();
    rd.setWList(wList);
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    tp.setRawData(rd);
    tp.setBestList(bestTrailingTrades);
    tp.setStrat(StratEnum.CANALADX);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        InputModel im = new InputModel();
        im.setAdxLookback(adxLookback);
        im.setCanalList(canalList);
        im.setCanalLookback(canalLookback);
        im.setAdx(i);
        im.setCandleList(candleList);
        im.setAdxList(adxList.get(i));
        im.setDiList(diList.get(i));
        im.setWholeKijunList(kijunList);
        im.setPoisonPill(0);
        im.setWList(wList);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      try {
        InputModel im = new InputModel();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new CanalAdxThread(blockingQueue, blockingQueueOut, bestTrailingTrades, 10)).start();
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

    Trades t = adxStrat.adxOnlyLongDetails((kijunCount - 1), kijunList.get((kijunCount - 1)), goLong, closeLong,
        (adxCount - 1), adxList.get(adxCount - 1), diList.get(adxCount - 1), candleList);
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
    System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
        + " | cThr:" + t.getCloseLongThreshold() + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
        + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format(((profitableLongs) / t.getTradePercentages().size())) + " | L:" + df.format(t.getLowest())
        + " | pL:" + profitableLongs + " | nL:" + negativeLongs);

  }

  private static void bestAdxLongOnly(int adxCount, int kijunCount, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestAdxLongOnly(" + adxCount + ", " + kijunCount + ", " + consumers + ")");

    List<Trades> bestAdxLongOnlyTrades = new ArrayList<>();

    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    tp.setRawData(rd);
    tp.setBestList(bestAdxLongOnlyTrades);
    tp.setStrat(StratEnum.ADXLONGONLY);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        InputModel im = new InputModel();
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
        InputModel im = new InputModel();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AdxLongOnlyThread(blockingQueue, blockingQueueOut, bestAdxLongOnlyTrades, 10)).start();
    }

  }

  private static void bestAdxShortOnly(int adxCount, int kijunCount, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestAdxShortOnly(" + adxCount + ", " + kijunCount + ", " + consumers + ")");

    List<Trades> bestAdxShortOnlyTrades = new ArrayList<>();

    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    tp.setRawData(rd);
    tp.setBestList(bestAdxShortOnlyTrades);
    tp.setStrat(StratEnum.ADXSHORTONLY);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        InputModel im = new InputModel();
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
        InputModel im = new InputModel();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new AdxShortOnlyThread(blockingQueue, blockingQueueOut, bestAdxShortOnlyTrades, 10)).start();
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

    Trades t = adxStrat.adxTrailingTradesDetails((kijunCount - 1), kijunList.get((kijunCount - 1)), highTre, lowTre,
        (adxCount - 1), adxList.get(adxCount - 1), diList.get(adxCount - 1), atrCount,
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
    DecimalFormat df = new DecimalFormat("#.####");
    System.out.println("adx:" + (t.getAdx() + 1) + " | kij:" + (t.getKijun() + 1) + " | thr:" + t.getAdxThreshold()
        + " | atr:" + t.getAtr() + " | mu:" + multi + " | pro:" + df.format(t.getProfit()) + " | aP:" + df.format(avgP)
        + " | aN:" + df.format(avgN) + " | tC:" + t.getTradePercentages().size() + " | t%:"
        + df.format(((profitableLongs + profitableShorts) / t.getTradePercentages().size())) + " | L:"
        + df.format(t.getLowest()) + " | pL:" + profitableLongs + " | nL:" + negativeLongs + " | pS:" + profitableShorts
        + " | nS:" + negativeShorts);
    System.out.println("negativeSize > " + t.getTop5Negative().size());
  }

  private static void bestAdxTrailing(int adxCount, int kijunCount, int atrCount, float maxMultiplier, int consumers,
      int printHowMany, List<Candlestick> candleList, List<Candlestick> candleList1h) {
    System.out.println(csvFile + "\nbestAdxTrailing(" + adxCount + ", " + kijunCount + ", " + atrCount + ", "
        + maxMultiplier + ", " + consumers + ")");

    AtrUtil atr = new AtrUtil();
    List<Trades> bestTrailingTrades = new ArrayList<>();
    List<List<List<Float>>> atrTrailingMultipliesList = createAtrMultiplierAtrTrailingList(atrCount, maxMultiplier,
        candleList1h, atr);
    KijunUtil kijun = new KijunUtil();
    ADXutil adx = new ADXutil();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    RawData rd = new RawData();
    rd.setAtrTrailingMultipliesList(atrTrailingMultipliesList);
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    rd.setCandleList1h(candleList1h);
    tp.setRawData(rd);
    tp.setBestList(bestTrailingTrades);
    tp.setStrat(StratEnum.ADXTRAILING);
    new Thread(tp).start();

    for (int i = 5; i < adxCount; i++) {
      try {
        InputModel im = new InputModel();
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
        InputModel im = new InputModel();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new ADXTrailingThread(blockingQueue, blockingQueueOut, bestTrailingTrades, 10)).start();
    }

  }

  private static void bestTrailingStop(int atrCount, float maxMultiplier, int consumers, int printHowMany,
      List<Candlestick> candleList) {
    System.out.println(csvFile + "\nbestTrailingStop(" + atrCount + ", " + maxMultiplier + ", " + consumers + ")");
    AtrUtil atr = new AtrUtil();
    List<Trades> bestTrailingTrades = new ArrayList<>();
    List<List<List<Boolean>>> atrMultipliesIsLongList = createAtrMultiplierIsLongList(atrCount, maxMultiplier,
        candleList, atr);

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setBestList(bestTrailingTrades);
    tp.setStrat(StratEnum.TRAILINGSTOP);
    new Thread(tp).start();

    for (int i = 5; i < atrCount; i++) {
      try {
        InputModel im = new InputModel();
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
        InputModel im = new InputModel();
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
    List<Trades> bestAdxPortionesultList = new ArrayList<>();
    List<Kijun> kijunList = new ArrayList<>();
    List<List<Float>> adxList = new ArrayList<List<Float>>();
    List<DI> diList = createDIList(adxCount, adxList, candleList, adx);
    createKijunList(kijunCount, kijunList, candleList, kijun);
    createADXList(adxCount, adxList, candleList, adx);

    BlockingQueue<InputModel> blockingQueue = new LinkedBlockingDeque<>();
    BlockingQueue<Integer> blockingQueueOut = new LinkedBlockingDeque<>();
    RawData rd = new RawData();
    rd.setAdxList(adxList);
    rd.setDiList(diList);
    rd.setKijunList(kijunList);
    rd.setCandleList(candleList);
    TradesPrinter tp = new TradesPrinter(blockingQueueOut, printHowMany, consumers);
    tp.setBestList(bestAdxPortionesultList);
    tp.setRawData(rd);
    tp.setStrat(StratEnum.ADXPORTION);
    new Thread(tp).start();

    for (int i = 8; i < adxCount; i++) {
      try {
        InputModel im = new InputModel();
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
        InputModel im = new InputModel();
        im.setPoisonPill(10);
        blockingQueue.put(im);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < consumers; i++) {
      new Thread(new ADXPortionThread(blockingQueue, blockingQueueOut, bestAdxPortionesultList, 10)).start();
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
