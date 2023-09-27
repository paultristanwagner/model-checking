package me.paultristanwagner.modelchecking.ctlstar.formula;

public class CTLStarParenthesisFormula extends CTLStarFormula {

  private final CTLStarFormula formula;

  private CTLStarParenthesisFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarParenthesisFormula parenthesis(CTLStarFormula formula) {
    return new CTLStarParenthesisFormula(formula);
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return "(" + formula + ")";
  }
}
