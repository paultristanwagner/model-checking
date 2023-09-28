package me.paultristanwagner.modelchecking.ctlstar.formula;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLIdentifierFormula;

public class CTLStarIdentifierFormula extends CTLStarFormula {

  private final String identifier;

  private CTLStarIdentifierFormula(String identifier) {
    this.identifier = identifier;
  }

  public static CTLStarIdentifierFormula identifier(String identifier) {
    return new CTLStarIdentifierFormula(identifier);
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
    return LTLIdentifierFormula.identifier(identifier);
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return identifier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CTLStarIdentifierFormula other) {
      return identifier.equals(other.identifier);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier);
  }
}
