package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.util.Symbol;

public class CTLStarAllFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarAllFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarAllFormula all(CTLStarFormula formula) {
    return new CTLStarAllFormula(formula);
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
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = formula.getSubFormulas();
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    throw new UnsupportedOperationException(
        "CTL* to LTL conversion is not supported for CTL* all formulas");
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return Symbol.UNIVERSAL_QUANTIFIER_SYMBOL + formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarAllFormula that = (CTLStarAllFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
