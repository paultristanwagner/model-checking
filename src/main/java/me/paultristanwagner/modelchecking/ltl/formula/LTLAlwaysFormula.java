package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.ALWAYS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLAlwaysFormula extends LTLFormula {

  private final LTLFormula formula;

  private LTLAlwaysFormula(LTLFormula formula) {
    this.formula = formula;
  }

  public static LTLAlwaysFormula always(LTLFormula formula) {
    return new LTLAlwaysFormula(formula);
  }

  public LTLFormula getFormula() {
    return formula;
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    subformulas.addAll(formula.getAllSubformulas());
    return subformulas;
  }

  @Override
  public String toString() {
    return ALWAYS + formula;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLAlwaysFormula alwaysFormula && formula.equals(alwaysFormula.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
