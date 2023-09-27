package me.paultristanwagner.modelchecking.ctl.formula.path;

import static me.paultristanwagner.modelchecking.util.Symbol.ALWAYS_SYMBOL;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula;

public class CTLAlwaysFormula extends CTLPathFormula {

  private final CTLFormula stateFormula;

  private CTLAlwaysFormula(CTLFormula stateFormula) {
    this.stateFormula = stateFormula;
  }

  public static CTLAlwaysFormula always(CTLFormula stateFormula) {
    return new CTLAlwaysFormula(stateFormula);
  }

  public CTLFormula getStateFormula() {
    return stateFormula;
  }

  @Override
  public String toString() {
    if (stateFormula instanceof CTLParenthesisFormula) {
      return ALWAYS_SYMBOL + stateFormula;
    } else {
      return ALWAYS_SYMBOL + "(" + stateFormula + ")";
    }
  }
}
