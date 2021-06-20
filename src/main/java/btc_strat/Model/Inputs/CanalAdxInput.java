package btc_strat.Model.Inputs;

import java.util.List;

import btc_strat.Model.Canal;

public class CanalAdxInput extends AdxInput {
  List<Canal> canalList;
  int canalLookback;
  List<Float> williamList;

  public CanalAdxInput() {
  }

  public List<Canal> getCanalList() {
    return this.canalList;
  }

  public void setCanalList(List<Canal> canalList) {
    this.canalList = canalList;
  }

  public int getCanalLookback() {
    return this.canalLookback;
  }

  public void setCanalLookback(int canalLookback) {
    this.canalLookback = canalLookback;
  }

  public List<Float> getWilliamList() {
    return this.williamList;
  }

  public void setWilliamList(List<Float> williamList) {
    this.williamList = williamList;
  }
}
