package btc_strat.Model.Trades;

public class AlligatorFractalTrades extends Trades {
  int lip;
  int teeth;
  int jaw;
  int lipShift;
  int teethShift;
  int jawShift;
  int stopsHit;

  public AlligatorFractalTrades() {
  }

  public int getLip() {
    return this.lip;
  }

  public void setLip(int lip) {
    this.lip = lip;
  }

  public int getTeeth() {
    return this.teeth;
  }

  public void setTeeth(int teeth) {
    this.teeth = teeth;
  }

  public int getJaw() {
    return this.jaw;
  }

  public void setJaw(int jaw) {
    this.jaw = jaw;
  }

  public int getLipShift() {
    return this.lipShift;
  }

  public void setLipShift(int lipShift) {
    this.lipShift = lipShift;
  }

  public int getTeethShift() {
    return this.teethShift;
  }

  public void setTeethShift(int teethShift) {
    this.teethShift = teethShift;
  }

  public int getJawShift() {
    return this.jawShift;
  }

  public void setJawShift(int jawShift) {
    this.jawShift = jawShift;
  }

  public int getStopsHit() {
    return this.stopsHit;
  }

  public void setStopsHit(int stopsHit) {
    this.stopsHit = stopsHit;
  }

}
