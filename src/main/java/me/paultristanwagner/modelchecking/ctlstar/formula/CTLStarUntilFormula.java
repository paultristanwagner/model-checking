package me.paultristanwagner.modelchecking.ctlstar.formula;

public class CTLStarUntilFormula extends CTLStarFormula {

  private final CTLStarFormula left;
  private final CTLStarFormula right;

  private CTLStarUntilFormula(CTLStarFormula left, CTLStarFormula right) {
    this.left = left;
    this.right = right;
  }

  public static CTLStarUntilFormula until(CTLStarFormula left, CTLStarFormula right) {
    return new CTLStarUntilFormula(left, right);
  }

  public CTLStarFormula getLeft() {
    return left;
  }

  public CTLStarFormula getRight() {
    return right;
  }

  @Override
  public String toString() {
    // todo: add brackets, but also possibility to parse them properly
    return left + " U " + right;
  }
}
