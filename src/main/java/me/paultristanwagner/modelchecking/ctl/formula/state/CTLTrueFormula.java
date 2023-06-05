package me.paultristanwagner.modelchecking.ctl.formula.state;

public class CTLTrueFormula extends CTLFormula {

  private CTLTrueFormula() {
  }

    public static CTLTrueFormula TRUE() {
        return new CTLTrueFormula();
    }

  @Override
  public String toString() {
    return "true";
  }
}
