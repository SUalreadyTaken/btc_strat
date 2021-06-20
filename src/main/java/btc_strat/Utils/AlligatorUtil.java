package btc_strat.Utils;

import java.util.ArrayList;
import java.util.List;

import btc_strat.Model.Alligator;
import btc_strat.Model.Candlestick;
import btc_strat.Model.Ssma;

public class AlligatorUtil {

  public AlligatorUtil() {
  }

  public List<Alligator> getAlligator(List<Candlestick> candleList) {
    List<Alligator> result = new ArrayList<>();
    float lips5Total = 0;
    float teeth8Total = 0;
    float jaw13Total = 0;

    List<Float> lipsList = new ArrayList<>();
    List<Float> teethList = new ArrayList<>();
    List<Float> jawList = new ArrayList<>();

    for (int i = 0; i < 13; i++) {
      if (i < 5)
        lips5Total += (candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2;
      if (i < 8)
        teeth8Total += (candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2;
      if (i < 13)
        jaw13Total += (candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2;
      lipsList.add(0.0f);
      teethList.add(0.0f);
      jawList.add(0.0f);
    }

    float lips5F = lips5Total / 5;
    float teeth8F = teeth8Total / 8;
    float jaw13F = jaw13Total / 13;

    lipsList.add(lips5F);
    teethList.add(teeth8F);
    jawList.add(jaw13F);

    lipsList.add(((lips5F * 4) - lips5F + ((candleList.get(13).getHigh() + candleList.get(13).getLow()) / 2)) / 5);
    teethList.add(((teeth8F * 7) - teeth8F + ((candleList.get(13).getHigh() + candleList.get(13).getLow()) / 2)) / 8);
    jawList.add(((jaw13F * 12) - jaw13F + ((candleList.get(13).getHigh() + candleList.get(13).getLow()) / 2)) / 13);

    for (int i = 15; i < candleList.size(); i++) {
      lipsList.add(((lipsList.get(i - 1) * 5) - lipsList.get(i - 1)
          + ((candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2)) / 5);
      teethList.add(((teethList.get(i - 1) * 8) - teethList.get(i - 1)
          + ((candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2)) / 8);
      jawList.add(((jawList.get(i - 1) * 13) - jawList.get(i - 1)
          + ((candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2)) / 13);
    }

    // 8 5 3
    shiftToRight(lipsList, 3);
    shiftToRight(teethList, 5);
    shiftToRight(jawList, 8);

    for (int i = 0; i < candleList.size(); i++) {
      result.add(new Alligator(jawList.get(i), teethList.get(i), lipsList.get(i)));
    }
    return result;
  }

  private void shiftToRight(List<Float> jawList, int n) {
    for (int i = 0; i < n; i++) {
      float last = jawList.get(jawList.size() - 1);
      for (int j = jawList.size() - 1; j > 0; j--) {
        jawList.set(j, jawList.get(j - 1));
      }
      jawList.set(0, last);
    }
  }

  public Ssma getSsma(List<Candlestick> candleList, int length, int shift) {
    List<Float> values = new ArrayList<>();
    float total = 0;
    for (int i = 0; i < length; i++) {
      total += (candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2;
      values.add(0.0f);
    }
    float first = total / length;
    values.add(first);
    values.add(
        ((first * (shift - 1)) - first + ((candleList.get(length).getHigh() + candleList.get(length).getLow()) / 2))
            / length);
    for (int i = length + 2; i < candleList.size(); i++) {
      values.add(((values.get(i - 1) * length) - values.get(i - 1)
          + ((candleList.get(i).getHigh() + candleList.get(i).getLow()) / 2)) / length);
    }
    List<List<Float>> shiftedValues = new ArrayList<>();
    for (int i = 1; i < shift + 1; i++) {
      List<Float> tmp = copyList(values);
      shiftToRight(tmp, i);
      shiftedValues.add(tmp);
    }
    return new Ssma(length, shiftedValues);
  }

  private List<Float> copyList(List<Float> values) {
    List<Float> result = new ArrayList<>();
    for (int i = 0; i < values.size(); i++) {
      result.add(values.get(i));
    }
    return result;
  }
}
