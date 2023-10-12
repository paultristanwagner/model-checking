package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.UNTIL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLUntilFormula extends LTLFormula {

  private final LTLFormula left;
  private final LTLFormula right;

  private LTLUntilFormula(LTLFormula left, LTLFormula right) {
    this.left = left;
    this.right = right;
  }

  public static LTLFormula until(LTLFormula left, LTLFormula right) {
    return new LTLUntilFormula(left, right);
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    subformulas.addAll(left.getAllSubformulas());
    subformulas.addAll(right.getAllSubformulas());
    return subformulas;
  }

  public LTLFormula getLeft() {
    return left;
  }

  public LTLFormula getRight() {
    return right;
  }

  @Override
  public String toString() {
    return "(" + left + " " + UNTIL + " " + right + ")";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLUntilFormula untilFormula
        && left.equals(untilFormula.left)
        && right.equals(untilFormula.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
