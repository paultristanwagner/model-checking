package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLNextFormula;

public class CTLStarNextFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarNextFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarNextFormula next(CTLStarFormula formula) {
    return new CTLStarNextFormula(formula);
  }

  @Override
  public int getDepth() {
    return formula.getDepth() + 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    if (formula.equals(target)) {
      formula = CTLStarIdentifierFormula.identifier(freshVariable);
    } else {
      formula.replaceFormula(target, freshVariable);
    }
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = formula.getSubFormulas();
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    return LTLNextFormula.next(formula.toLTL());
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return NEXT + formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarNextFormula that = (CTLStarNextFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
