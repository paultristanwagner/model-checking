package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLParenthesisFormula extends LTLFormula {

  private final LTLFormula formula;

  private LTLParenthesisFormula(LTLFormula formula) {
    this.formula = formula;
  }

  public static LTLParenthesisFormula parenthesis(LTLFormula formula) {
    return new LTLParenthesisFormula(formula);
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    subformulas.addAll(formula.getAllSubformulas());
    return subformulas;
  }

  public LTLFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    if (formula instanceof LTLUntilFormula || formula instanceof LTLParenthesisFormula) {
      return formula.toString();
    }

    return "(" + formula + ")";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLParenthesisFormula parenthesisFormula
        && formula.equals(parenthesisFormula.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
