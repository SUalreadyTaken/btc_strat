package btc_strat.Model;

public class MrResult {
  float _Y;
  float _Y_f;
  float _err;

  public MrResult(float _Y, float _Y_f, float _err) {
    this._Y = _Y;
    this._Y_f = _Y_f;
    this._err = _err;
  }

  public float get_Y() {
    return this._Y;
  }

  public void set_Y(float _Y) {
    this._Y = _Y;
  }

  public float get_Y_f() {
    return this._Y_f;
  }

  public void set_Y_f(float _Y_f) {
    this._Y_f = _Y_f;
  }

  public float get_err() {
    return this._err;
  }

  public void set_err(float _err) {
    this._err = _err;
  }

}
