package me.paultristanwagner.modelchecking.ctl.formula.state;

import static me.paultristanwagner.modelchecking.util.Symbol.EXISTENTIAL_QUANTIFIER;

import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;

public class CTLExistsFormula extends CTLFormula {

  private final CTLPathFormula pathFormula;

  private CTLExistsFormula(CTLPathFormula pathFormula) {
    this.pathFormula = pathFormula;
  }

  public static CTLExistsFormula exists(CTLPathFormula pathFormula) {
    return new CTLExistsFormula(pathFormula);
  }

  public CTLPathFormula getPathFormula() {
    return pathFormula;
  }

  @Override
  public String toString() {
    return EXISTENTIAL_QUANTIFIER + pathFormula.toString();
  }
}
