package btc_strat.Model;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class Details {
  float open;
  float close;
  boolean position;
  LocalDateTime openDate;
  LocalDateTime closeDate;
  float pro;
  float percentage;
  DecimalFormat df = new DecimalFormat("#.###");
  float couldTake;
  LocalDateTime peakeDate;

  public Details(float open, float close, boolean position, LocalDateTime openDate, LocalDateTime closeDate, float pro,
      float percentage) {
    this.open = open;
    this.close = close;
    this.position = position;
    this.openDate = openDate;
    this.closeDate = closeDate;
    this.pro = pro;
    this.percentage = percentage;
  }

  public void setPeakeDate(LocalDateTime peakeDate) {
    this.peakeDate = peakeDate;
  }

  public LocalDateTime getPeakeDate() {
    return this.peakeDate;
  }

  public float getCouldTake() {
    return this.couldTake;
  }

  public void setCouldTake(float couldTake) {
    this.couldTake = couldTake;
  }

  public float getOpen() {
    return this.open;
  }

  public void setOpen(float open) {
    this.open = open;
  }

  public float getClose() {
    return this.close;
  }

  public void setClose(float close) {
    this.close = close;
  }

  public boolean isPosition() {
    return this.position;
  }

  public boolean getPosition() {
    return this.position;
  }

  public void setPosition(boolean position) {
    this.position = position;
  }

  public LocalDateTime getOpenDate() {
    return this.openDate;
  }

  public void setOpenDate(LocalDateTime openDate) {
    this.openDate = openDate;
  }

  public LocalDateTime getCloseDate() {
    return this.closeDate;
  }

  public void setCloseDate(LocalDateTime closeDate) {
    this.closeDate = closeDate;
  }

  public float getPro() {
    return this.pro;
  }

  public void setPro(float pro) {
    this.pro = pro;
  }

  public float getPercentage() {
    return this.percentage;
  }

  public void setPercentage(float percentage) {
    this.percentage = percentage;
  }

  public String couldTaketoString() {
    return "{" + " open:'" + getOpen() + "'" + ", close:'" + getClose() + "'" + ", pos:'" + isPosition() + "'"
        + ", oDate:'" + getOpenDate() + "'" + ", cDate:'" + getCloseDate() + "'" + ", pro:'" + df.format(getPro()) + "'"
        + ", per:'" + df.format(getPercentage()) + "'" + ", perC:'" + df.format(getCouldTake()) + "'" + ", perC:'" + getPeakeDate() + "'" + "}";
  }

  @Override
  public String toString() {
    return "{" + " open:'" + getOpen() + "'" + ", close:'" + getClose() + "'" + ", pos:'" + isPosition() + "'"
        + ", oDate:'" + getOpenDate() + "'" + ", cDate:'" + getCloseDate() + "'" + ", pro:'" + getPro() + "'"
        + ", per:'" + df.format(getPercentage()) + "'" + "}";
  }

}