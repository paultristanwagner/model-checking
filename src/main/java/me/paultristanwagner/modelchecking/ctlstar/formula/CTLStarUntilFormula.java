package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLUntilFormula;

public class CTLStarUntilFormula extends CTLStarFormula {

  private CTLStarFormula left;
  private CTLStarFormula right;

  private CTLStarUntilFormula(CTLStarFormula left, CTLStarFormula right) {
    this.left = left;
    this.right = right;
  }

  public static CTLStarUntilFormula until(CTLStarFormula left, CTLStarFormula right) {
    return new CTLStarUntilFormula(left, right);
  }

  @Override
  public int getDepth() {
    return Math.max(left.getDepth(), right.getDepth()) + 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    if (left.equals(target)) {
      left = CTLStarIdentifierFormula.identifier(freshVariable);
    } else {
      left.replaceFormula(target, freshVariable);
    }

    if (right.equals(target)) {
      right = CTLStarIdentifierFormula.identifier(freshVariable);
    } else {
      right.replaceFormula(target, freshVariable);
    }
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = left.getSubFormulas();
    subFormulas.addAll(right.getSubFormulas());
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    return LTLUntilFormula.until(left.toLTL(), right.toLTL());
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarUntilFormula that = (CTLStarUntilFormula) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
