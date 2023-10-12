package me.paultristanwagner.modelchecking.ctl.formula.state;

import static me.paultristanwagner.modelchecking.util.Symbol.UNIVERSAL_QUANTIFIER;

import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;

public class CTLAllFormula extends CTLFormula {

  private final CTLPathFormula pathFormula;

  private CTLAllFormula(CTLPathFormula pathFormula) {
    this.pathFormula = pathFormula;
  }

  public static CTLAllFormula forAll(CTLPathFormula pathFormula) {
    return new CTLAllFormula(pathFormula);
  }

  public CTLPathFormula getPathFormula() {
    return pathFormula;
  }

  @Override
  public String toString() {
    return UNIVERSAL_QUANTIFIER + pathFormula.toString();
  }
}
