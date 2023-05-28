package me.paultristanwagner.modelchecking.ctl.formula.state;

public class CTLIdentifierFormula extends CTLFormula {
  
  private final String identifier;
  
  private CTLIdentifierFormula( String identifier ) {
    this.identifier = identifier;
  }
  
  public static CTLIdentifierFormula of( String identifier ) {
    return new CTLIdentifierFormula( identifier );
  }
  
  public String getIdentifier() {
    return identifier;
  }
  
  @Override
  public String toString() {
    return identifier;
  }
}
