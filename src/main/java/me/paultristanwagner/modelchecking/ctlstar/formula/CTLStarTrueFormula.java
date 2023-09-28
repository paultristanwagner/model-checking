package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLTrueFormula;

public class CTLStarTrueFormula extends CTLStarFormula {

  private CTLStarTrueFormula() {}

  @Override
  public int getDepth() {
    return 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    // nothing to do
  }

  public static CTLStarTrueFormula TRUE() {
    return new CTLStarTrueFormula();
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = new HashSet<>();
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    return LTLTrueFormula.TRUE();
  }

  @Override
  public String toString() {
    return "true";
  }

  @Override
  public int hashCode() {
    return Objects.hash(1);
  }
}
