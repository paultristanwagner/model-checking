package me.paultristanwagner.modelchecking.ctl.formula.path;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.ALWAYS_SYMBOL;

public class CTLAlwaysFormula extends CTLPathFormula {
  
  private final CTLFormula stateFormula;
  
  private CTLAlwaysFormula(CTLFormula stateFormula) {
    this.stateFormula = stateFormula;
  }
  
  public static CTLAlwaysFormula of(CTLFormula stateFormula) {
    return new CTLAlwaysFormula( stateFormula );
  }
  
  public CTLFormula getStateFormula() {
    return stateFormula;
  }
  
  @Override
  public String toString() {
    if(stateFormula instanceof CTLParenthesisFormula ) {
      return ALWAYS_SYMBOL + stateFormula;
    } else {
      return ALWAYS_SYMBOL + "(" + stateFormula + ")";
    }
  }
}
