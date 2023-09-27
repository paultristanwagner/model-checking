package me.paultristanwagner.modelchecking.ctlstar.formula;

public class CTLStarTrueFormula extends CTLStarFormula {

  private CTLStarTrueFormula() {}

  public static CTLStarTrueFormula TRUE() {
    return new CTLStarTrueFormula();
  }

  @Override
  public String toString() {
    return "true";
  }
}
