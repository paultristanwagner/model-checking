package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT_SYMBOL;

public class CTLStarNextFormula extends CTLStarFormula {

  private final CTLStarFormula formula;

  private CTLStarNextFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarNextFormula next(CTLStarFormula formula) {
    return new CTLStarNextFormula(formula);
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return NEXT_SYMBOL + formula;
  }
}
