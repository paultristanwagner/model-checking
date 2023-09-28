package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.Set;
import me.paultristanwagner.modelchecking.Formula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;

public abstract class CTLStarFormula extends Formula {

  public abstract int getDepth();

  public abstract void replaceFormula(CTLStarFormula target, String freshVariable);

  public abstract Set<CTLStarFormula> getSubFormulas();

  public Set<CTLStarFormula> getProperSubFormulas() {
    Set<CTLStarFormula> result = getSubFormulas();
    result.remove(this);
    return result;
  }

  public abstract LTLFormula toLTL();
}
