package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NOT_SYMBOL;

public class CTLStarNotFormula extends CTLStarFormula {

  private final CTLStarFormula formula;

  private CTLStarNotFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarNotFormula not(CTLStarFormula formula) {
    return new CTLStarNotFormula(formula);
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return NOT_SYMBOL + formula;
  }
}
