package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLTrueFormula extends LTLFormula {

  private LTLTrueFormula() {}

  public static LTLTrueFormula TRUE() {
    return new LTLTrueFormula();
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    return subformulas;
  }

  @Override
  public String toString() {
    return "true";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLTrueFormula;
  }

  @Override
  public int hashCode() {
    return Objects.hash(true);
  }
}
