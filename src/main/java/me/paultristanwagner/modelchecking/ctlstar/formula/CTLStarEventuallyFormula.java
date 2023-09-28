package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarIdentifierFormula.identifier;

import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLEventuallyFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.util.Symbol;

public class CTLStarEventuallyFormula extends CTLStarFormula {

  private CTLStarFormula formula;

  private CTLStarEventuallyFormula(CTLStarFormula formula) {
    this.formula = formula;
  }

  public static CTLStarEventuallyFormula eventually(CTLStarFormula formula) {
    return new CTLStarEventuallyFormula(formula);
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
      formula = identifier(freshVariable);
    } else {
      formula.replaceFormula(target, freshVariable);
    }
  }

  @Override
  public LTLFormula toLTL() {
    return LTLEventuallyFormula.eventually(formula.toLTL());
  }

  @Override
  public String toString() {
    return Symbol.EVENTUALLY_SYMBOL + formula;
  }

  public CTLStarFormula getFormula() {
    return formula;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarEventuallyFormula that = (CTLStarEventuallyFormula) o;
    return Objects.equals(formula, that.formula);
  }

  @Override
  public int hashCode() {
    return Objects.hash(formula);
  }
}
