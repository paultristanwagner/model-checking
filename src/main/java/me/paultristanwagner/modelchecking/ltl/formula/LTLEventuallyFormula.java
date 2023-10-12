package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.EVENTUALLY;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLEventuallyFormula extends LTLFormula {

  private final LTLFormula formula;

  private LTLEventuallyFormula(LTLFormula formula) {
    this.formula = formula;
  }

  public static LTLEventuallyFormula eventually(LTLFormula formula) {
    return new LTLEventuallyFormula(formula);
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
  public boolean equals(Object obj) {
    return obj instanceof LTLEventuallyFormula eventuallyFormula
        && formula.equals(eventuallyFormula.formula);
  }

  @Override
  public String toString() {
    return EVENTUALLY + formula;
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
