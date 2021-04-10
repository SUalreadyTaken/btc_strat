package btc_strat.Model;

import java.time.LocalDateTime;

public class NegativeDetail {
  float negative;
  LocalDateTime date;

  public NegativeDetail(float negative, LocalDateTime date) {
    this.negative = negative;
    this.date = date;
  }

  public float getNegative() {
    return this.negative;
  }

  public void setNegative(float negative) {
    this.negative = negative;
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }


  @Override
  public String toString() {
    return "{" +
      " negative='" + getNegative() + "'" +
      ", date='" + getDate() + "'" +
      "}";
  }

}