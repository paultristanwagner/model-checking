package me.paultristanwagner.modelchecking.ctl.formula.path;

import static me.paultristanwagner.modelchecking.util.Symbol.EVENTUALLY;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula;

public class CTLEventuallyFormula extends CTLPathFormula {

  private final CTLFormula stateFormula;

  private CTLEventuallyFormula(CTLFormula stateFormula) {
    this.stateFormula = stateFormula;
  }

  public static CTLEventuallyFormula eventually(CTLFormula stateFormula) {
    return new CTLEventuallyFormula(stateFormula);
  }

  public CTLFormula getStateFormula() {
    return stateFormula;
  }

  @Override
  public String toString() {
    if (stateFormula instanceof CTLParenthesisFormula) {
      return EVENTUALLY + stateFormula;
    } else {
      return EVENTUALLY + "(" + stateFormula + ")";
    }
  }
}
