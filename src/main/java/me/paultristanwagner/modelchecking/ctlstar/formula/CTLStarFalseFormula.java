package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFalseFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;

public class CTLStarFalseFormula extends CTLStarFormula {

  private CTLStarFalseFormula() {}

  public static CTLStarFalseFormula FALSE() {
    return new CTLStarFalseFormula();
  }

  @Override
  public int getDepth() {
    return 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    // nothing to do
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = new HashSet<>();
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    return LTLFalseFormula.FALSE();
  }

  @Override
  public String toString() {
    return "false";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CTLStarFalseFormula;
  }

  @Override
  public int hashCode() {
    return Objects.hash(false);
  }
}
