package me.paultristanwagner.modelchecking.ctl.formula.path;

import static me.paultristanwagner.modelchecking.util.Symbol.UNTIL;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;

public class CTLUntilFormula extends CTLPathFormula {

  private final CTLFormula left;
  private final CTLFormula right;

  private CTLUntilFormula(CTLFormula left, CTLFormula right) {
    this.left = left;
    this.right = right;
  }

  public static CTLUntilFormula until(CTLFormula left, CTLFormula right) {
    return new CTLUntilFormula(left, right);
  }

  public CTLFormula getLeft() {
    return left;
  }

  public CTLFormula getRight() {
    return right;
  }

  @Override
  public String toString() {
    return "(" + left.toString() + " " + UNTIL + " " + right.toString() + ")";
  }
}
