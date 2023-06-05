package me.paultristanwagner.modelchecking.ctl.formula.path;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT_SYMBOL;

public class CTLNextFormula extends CTLPathFormula {
  
  private final CTLFormula stateFormula;
  
  private CTLNextFormula( CTLFormula stateFormula ) {
    this.stateFormula = stateFormula;
  }
  
  public static CTLNextFormula of( CTLFormula stateFormula ) {
    return new CTLNextFormula( stateFormula );
  }
  
  public CTLFormula getStateFormula() {
    return stateFormula;
  }
  
  @Override
  public String toString() {
    if(stateFormula instanceof CTLParenthesisFormula ) {
      return NEXT_SYMBOL + stateFormula;
    } else {
      return NEXT_SYMBOL + "(" + stateFormula + ")";
    }
  }
}
