package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT_SYMBOL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLNextFormula extends LTLFormula {

  private final LTLFormula formula;

  private LTLNextFormula(LTLFormula formula) {
    this.formula = formula;
  }

  public static LTLNextFormula next(LTLFormula formula) {
    return new LTLNextFormula(formula);
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
    return obj instanceof LTLNextFormula nextFormula && formula.equals(nextFormula.formula);
  }

  @Override
  public String toString() {
    return NEXT_SYMBOL + formula;
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
