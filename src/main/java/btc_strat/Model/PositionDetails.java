package btc_strat.Model;

import java.time.LocalDateTime;

public class PositionDetails {
  boolean position;
  float open;
  private LocalDateTime date;

  public PositionDetails(){}

  public PositionDetails(boolean position, float open, LocalDateTime date) {
    this.position = position;
    this.open = open;
    this.date = date;
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

  public float getOpen() {
    return this.open;
  }

  public void setOpen(float open) {
    this.open = open;
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }
  
}