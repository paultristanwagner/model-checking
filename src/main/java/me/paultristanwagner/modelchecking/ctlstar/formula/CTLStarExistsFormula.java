package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.EXISTENTIAL_QUANTIFIER;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;

public class CTLStarExistsFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarExistsFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarExistsFormula exists(CTLStarFormula formula) {
    return new CTLStarExistsFormula(formula);
  }

  public CTLStarFormula getFormula() {
    return formula;
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
    throw new UnsupportedOperationException(
        "CTL* to LTL conversion is not supported for existential quantifiers");
  }

  @Override
  public String toString() {
    return EXISTENTIAL_QUANTIFIER + formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarExistsFormula that = (CTLStarExistsFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
