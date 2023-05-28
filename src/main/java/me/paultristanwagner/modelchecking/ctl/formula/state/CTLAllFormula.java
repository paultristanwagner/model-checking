package me.paultristanwagner.modelchecking.ctl.formula.state;

import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.UNIVERSAL_QUANTIFIER_SYMBOL;

public class CTLAllFormula extends CTLFormula {
  
  private final CTLPathFormula pathFormula;
  
  private CTLAllFormula( CTLPathFormula pathFormula ) {
    this.pathFormula = pathFormula;
  }
  
  public static CTLAllFormula of( CTLPathFormula pathFormula ) {
    return new CTLAllFormula( pathFormula );
  }
  
  public CTLPathFormula getPathFormula() {
    return pathFormula;
  }
  
  @Override
  public String toString() {
    return UNIVERSAL_QUANTIFIER_SYMBOL + pathFormula.toString();
  }
}
