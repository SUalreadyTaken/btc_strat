package btc_strat.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import btc_strat.Model.Candlestick;
import btc_strat.Model.MRBands;
import btc_strat.Model.MrResult;

/**
 * From tradingview
 * Moving Regression Prediction Bands (MRBands)
// © tbiktag
 */
public class MRBandsUtil {

  public MRBands getBands(List<MrResult> mrResultList, float mult, int window) {
    List<Float> upperValues = new ArrayList<>();
    List<Float> lowerValues = new ArrayList<>();
    for (int i = 0; i < window + 1; i++) {
      upperValues.add(0.0f);
      lowerValues.add(0.0f);
    }
    float _err1 = 0.0f;
    float _yF1 = 0.0f;
    for (int i = window + 1; i < mrResultList.size(); i++) {
      _err1 = mrResultList.get(i).get_err();
      _yF1 = mrResultList.get(i).get_Y_f();
      upperValues.add(_yF1 + (_err1 * mult));
      lowerValues.add(_yF1 - (_err1 * mult));
    }
    return new MRBands(upperValues, lowerValues);
  }

  public List<MrResult> getMrList(List<Candlestick> candleList, int window, int degree) {
    List<MrResult> result = new ArrayList<>();
    for (int i = 0; i < window; i++) {
      result.add(null);
    }
    List<Float> _J = newFloat(window * (degree + 1), 0);
    for (int i = 0; i < window; i++) {
      for (int j = 0; j <= degree; j++) {
        matrixSet(_J, (float) Math.pow(i, j), i, j, window);
      }
    }
    for (int i = window + 1; i < candleList.size(); i++) {
      MrResult res = mr(_J, i - window - 1, candleList, window, degree);
      result.add(res);
    }
    return result;
  }

  public MrResult mr(List<Float> _I, int index, List<Candlestick> candleList, int window, int degree) {

    List<Candlestick> _Y_raw = new ArrayList<>();
    for (int i = 0; i < window; i++) {
      _Y_raw.add(null);
    }
    for (int i = 0; i < window; i++) {
      _Y_raw.set(i, candleList.get(index + i));
    }

    List<Float> _C = pinv(_I, window, degree + 1);
    List<Float> _a_coef = multiplyCandle(_C, _Y_raw, degree + 1, window, 1);
    float _Y = 0.0f;
    for (int i = 0; i <= degree; i++) {
      _Y = (float) (_Y + _a_coef.get(i) * Math.pow(window - 1, i));
    }
    float _Y_f = 0.0f;
    for (int i = 0; i <= degree; i++) {
      _Y_f = (float) (_Y_f + _a_coef.get(i) * Math.pow(window, i));
    }
    List<Float> _Y_hat = multiply(_I, _a_coef, window, degree + 1, 1);
    float _err = mae(_Y_raw, _Y_hat);
    return new MrResult(_Y, _Y_f, _err);
  }

  private float mae(List<Candlestick> _x, List<Float> _xhat) {
    float _mae = 0.0f;
    if (_x.size() != _xhat.size()) {
      return 0;
    } else {
      float _N = _x.size();
      for (int i = 0; i < _N; i++) {
        _mae = _mae + Math.abs((_x.get(i).getClose() - _xhat.get(i)) / _N);
      }
    }
    return _mae;
  }

  private List<Float> newFloat(int size, float value) {
    ArrayList<Float> arr = new ArrayList<Float>(Collections.nCopies(size, 0.0f));
    return arr;
  }

  private void matrixSet(List<Float> _J, float value, int i, int j, int window) {
    _J.set(i + window * j, value);
  }

  private Float matrixGet(List<Float> _J, int i, int j, int nrows) {
    return _J.get(i + nrows * j);
  }

  private Float matrixGetCandle(List<Candlestick> _J, int i, int j, int nrows) {
    return _J.get(i + nrows * j).getClose();
  }

  private List<Float> pinv(List<Float> _J, int window, int degree) {
    List<List<Float>> _Q_R = gr_diag(_J, window, degree);
    List<Float> _QT = transpose(_Q_R.get(0), window, degree);
    List<Float> _Rinv = newFloat(degree * degree, 0);
    float _r = 0.0f;
    matrixSet(_Rinv, 1 / matrixGet(_Q_R.get(1), 0, 0, degree), 0, 0, degree);
    if (degree != 1) {
      for (int j = 1; j < degree; j++) {
        for (int i = 0; i < j; i++) {
          _r = 0.0f;
          for (int k = i; k < j; k++) {
            _r = _r + matrixGet(_Rinv, i, k, degree) * matrixGet(_Q_R.get(1), k, j, degree);
          }
          matrixSet(_Rinv, _r, i, j, degree);
        }
        for (int k = 0; k < j; k++) {
          matrixSet(_Rinv, -matrixGet(_Rinv, k, j, degree) / matrixGet(_Q_R.get(1), j, j, degree), k, j, degree);
        }
        matrixSet(_Rinv, 1 / matrixGet(_Q_R.get(1), j, j, degree), j, j, degree);
      }
    }
    List<Float> _Ainv = multiply(_Rinv, _QT, degree, degree, window);
    return _Ainv;
  }

  private List<Float> multiply(List<Float> _A, List<Float> _B, int nrowsA, int ncolumnsA, int ncolumnsB) {
    List<Float> _C = newFloat(nrowsA * ncolumnsB, 0);
    int nrowsB = ncolumnsA;
    float elementC = 0.0f;
    for (int i = 0; i < nrowsA; i++) {
      for (int j = 0; j < ncolumnsB; j++) {
        elementC = 0.0f;
        for (int k = 0; k < ncolumnsA; k++) {
          elementC = elementC + matrixGet(_A, i, k, nrowsA) * matrixGet(_B, k, j, nrowsB);
        }
        matrixSet(_C, elementC, i, j, nrowsA);
      }
    }
    return _C;
  }

  private List<Float> multiplyCandle(List<Float> _A, List<Candlestick> _B, int nrowsA, int ncolumnsA, int ncolumnsB) {
    List<Float> _C = newFloat(nrowsA * ncolumnsB, 0);
    int nrowsB = ncolumnsA;
    float elementC = 0.0f;
    for (int i = 0; i < nrowsA; i++) {
      for (int j = 0; j < ncolumnsB; j++) {
        elementC = 0.0f;
        for (int k = 0; k < ncolumnsA; k++) {
          elementC = elementC + matrixGet(_A, i, k, nrowsA) * matrixGetCandle(_B, k, j, nrowsB);
        }
        matrixSet(_C, elementC, i, j, nrowsA);
      }
    }
    return _C;
  }

  private List<List<Float>> gr_diag(List<Float> _A, int nrows, int ncolumns) {
    List<Float> _Q = newFloat(nrows * ncolumns, 0);
    List<Float> _R = newFloat(ncolumns * ncolumns, 0);
    List<Float> _a = newFloat(nrows, 0);
    // List<Float> _q = newFloat(nrows, 0);
    float _r = 0.0f;
    float _aux = 0.0f;
    for (int i = 0; i < nrows; i++) {
      _a.set(i, matrixGet(_A, i, 0, nrows));
    }
    _r = vnorm(_a, nrows);
    matrixSet(_R, _r, 0, 0, ncolumns);

    for (int i = 0; i < nrows; i++) {
      matrixSet(_Q, _a.get(i) / _r, i, 0, nrows);
    }
    if (ncolumns != 1) {
      for (int k = 1; k < ncolumns; k++) {
        for (int i = 0; i < nrows; i++) {
          _a.set(i, matrixGet(_A, i, k, nrows));
        }
        for (int j = 0; j < k; j++) {
          _r = 0;
          for (int i = 0; i < nrows; i++) {
            _r = _r + matrixGet(_Q, i, j, nrows) * _a.get(i);
          }
          matrixSet(_R, _r, j, k, ncolumns);
          for (int i = 0; i < nrows; i++) {
            _aux = _a.get(i) - _r * matrixGet(_Q, i, j, nrows);
            _a.set(i, _aux);
          }
        }
        _r = vnorm(_a, nrows);
        matrixSet(_R, _r, k, k, ncolumns);
        for (int i = 0; i < nrows; i++) {
          matrixSet(_Q, _a.get(i) / _r, i, k, nrows);
        }
      }
    }
    List<List<Float>> res = new ArrayList<>();
    res.add(_Q);
    res.add(_R);
    return res;
  }

  private float vnorm(List<Float> _a, int window) {
    float _norm = 0.0f;
    for (int i = 0; i < window; i++) {
      _norm = (float) (_norm + Math.pow(_a.get(i), 2));
    }
    return (float) Math.sqrt(_norm);
  }

  private List<Float> transpose(List<Float> _A, int window, int degree) {
    List<Float> _AT = newFloat(window * degree, 0);
    for (int i = 0; i < window; i++) {
      for (int j = 0; j < degree; j++) {
        matrixSet(_AT, matrixGet(_A, i, j, window), j, i, degree);
      }
    }
    return _AT;
  }
}