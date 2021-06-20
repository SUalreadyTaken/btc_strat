package btc_strat.Model.Inputs;

import java.util.List;

import btc_strat.Model.FractalsDefault;
import btc_strat.Model.Ssma;

public class AlligatorFractalInput extends Input {
  int shift;
  int lip;
  int teeth;
  int jaw;
  List<Ssma> ssmaList;
  FractalsDefault fractalsDefault;

  public AlligatorFractalInput() {
  }

  public int getShift() {
    return this.shift;
  }

  public void setShift(int shift) {
    this.shift = shift;
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

  public List<Ssma> getSsmaList() {
    return this.ssmaList;
  }

  public void setSsmaList(List<Ssma> ssmaList) {
    this.ssmaList = ssmaList;
  }

  public FractalsDefault getFractalsDefault() {
    return this.fractalsDefault;
  }

  public void setFractalsDefault(FractalsDefault fractalsDefault) {
    this.fractalsDefault = fractalsDefault;
  }

}
