package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLAlwaysFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.util.Symbol;

public class CTLStarAlwaysFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarAlwaysFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarAlwaysFormula always(CTLStarFormula formula) {
    return new CTLStarAlwaysFormula(formula);
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = formula.getSubFormulas();
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public int getDepth() {
    return formula.getDepth() + 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    if (formula == target) {
      formula = CTLStarIdentifierFormula.identifier(freshVariable);
    } else {
      formula.replaceFormula(target, freshVariable);
    }
  }

  @Override
  public LTLFormula toLTL() {
    return LTLAlwaysFormula.always(formula.toLTL());
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return Symbol.ALWAYS_SYMBOL + formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarAlwaysFormula that = (CTLStarAlwaysFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
