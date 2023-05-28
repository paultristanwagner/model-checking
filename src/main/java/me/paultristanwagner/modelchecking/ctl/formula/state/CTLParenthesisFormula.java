package me.paultristanwagner.modelchecking.ctl.formula.state;

public class CTLParenthesisFormula extends CTLFormula {
  
  private final CTLFormula inner;
  
  private CTLParenthesisFormula(CTLFormula inner) {
    this.inner = inner;
  }
  
  public static CTLParenthesisFormula of(CTLFormula inner) {
    return new CTLParenthesisFormula( inner );
  }
  
  public CTLFormula getInner() {
    return inner;
  }
  
  @Override
  public String toString() {
    return "(" + inner + ")";
  }
}
