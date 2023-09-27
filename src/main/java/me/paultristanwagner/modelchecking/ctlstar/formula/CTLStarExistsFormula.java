package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.EXISTENTIAL_QUANTIFIER_SYMBOL;

public class CTLStarExistsFormula extends CTLStarFormula {

  private final CTLStarFormula formula;

  private CTLStarExistsFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarExistsFormula exists(CTLStarFormula formula) {
    return new CTLStarExistsFormula(formula);
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return EXISTENTIAL_QUANTIFIER_SYMBOL + formula;
  }
}
