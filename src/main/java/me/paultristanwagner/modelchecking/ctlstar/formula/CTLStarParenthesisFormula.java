package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLParenthesisFormula;

public class CTLStarParenthesisFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarParenthesisFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarParenthesisFormula parenthesis(CTLStarFormula formula) {
    return new CTLStarParenthesisFormula(formula);
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
    return LTLParenthesisFormula.parenthesis(formula.toLTL());
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return "(" + formula + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarParenthesisFormula that = (CTLStarParenthesisFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
