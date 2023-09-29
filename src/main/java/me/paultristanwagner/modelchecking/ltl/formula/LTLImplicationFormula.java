package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.IMPLICATION_SYMBOL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLImplicationFormula extends LTLFormula {

  private final LTLFormula left;
  private final LTLFormula right;

  private LTLImplicationFormula(LTLFormula left, LTLFormula right) {
    this.left = left;
    this.right = right;
  }

  public static LTLImplicationFormula implies(LTLFormula left, LTLFormula right) {
    return new LTLImplicationFormula(left, right);
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
    return "(" + left + " " + IMPLICATION_SYMBOL + " " + right + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LTLImplicationFormula that = (LTLImplicationFormula) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }
}
