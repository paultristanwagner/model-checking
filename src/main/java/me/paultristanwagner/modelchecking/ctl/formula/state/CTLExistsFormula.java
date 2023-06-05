package me.paultristanwagner.modelchecking.ctl.formula.state;

import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;

import static me.paultristanwagner.modelchecking.util.Symbol.EXISTENTIAL_QUANTIFIER_SYMBOL;

public class CTLExistsFormula extends CTLFormula {
  
  private final CTLPathFormula pathFormula;
  
  private CTLExistsFormula( CTLPathFormula pathFormula ) {
    this.pathFormula = pathFormula;
  }
  
  public static CTLExistsFormula of( CTLPathFormula pathFormula ) {
    return new CTLExistsFormula( pathFormula );
  }
  
  public CTLPathFormula getPathFormula() {
    return pathFormula;
  }
  
  @Override
  public String toString() {
    return EXISTENTIAL_QUANTIFIER_SYMBOL + pathFormula.toString();
  }
}
