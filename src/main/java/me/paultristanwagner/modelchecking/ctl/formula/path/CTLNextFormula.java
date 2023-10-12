package me.paultristanwagner.modelchecking.ctl.formula.path;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula;

public class CTLNextFormula extends CTLPathFormula {

  private final CTLFormula stateFormula;

  private CTLNextFormula(CTLFormula stateFormula) {
    this.stateFormula = stateFormula;
  }

  public static CTLNextFormula next(CTLFormula stateFormula) {
    return new CTLNextFormula(stateFormula);
  }

  public CTLFormula getStateFormula() {
    return stateFormula;
  }

  @Override
  public String toString() {
    if (stateFormula instanceof CTLParenthesisFormula) {
      return NEXT + stateFormula;
    } else {
      return NEXT + "(" + stateFormula + ")";
    }
  }
}
