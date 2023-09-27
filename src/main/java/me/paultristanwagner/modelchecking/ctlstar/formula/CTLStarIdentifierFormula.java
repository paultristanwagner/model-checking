package me.paultristanwagner.modelchecking.ctlstar.formula;

public class CTLStarIdentifierFormula extends CTLStarFormula {

  private final String identifier;

  private CTLStarIdentifierFormula(String identifier) {
    this.identifier = identifier;
  }

  public static CTLStarIdentifierFormula identifier(String identifier) {
    return new CTLStarIdentifierFormula(identifier);
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return identifier;
  }
}
