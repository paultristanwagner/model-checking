package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLFalseFormula extends LTLFormula {

  private LTLFalseFormula() {}

  public static LTLFalseFormula FALSE() {
    return new LTLFalseFormula();
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    return subformulas;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLFalseFormula;
  }

  @Override
  public String toString() {
    return "false";
  }

  @Override
  public int hashCode() {
    return Objects.hash(false);
  }
}
