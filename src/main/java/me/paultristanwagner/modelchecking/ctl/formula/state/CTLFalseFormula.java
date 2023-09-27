package me.paultristanwagner.modelchecking.ctl.formula.state;

public class CTLFalseFormula extends CTLFormula {

  private CTLFalseFormula() {}

  public static CTLFalseFormula FALSE() {
    return new CTLFalseFormula();
  }

  @Override
  public String toString() {
    return "false";
  }
}
