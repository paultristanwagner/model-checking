package me.paultristanwagner.modelchecking.ctl.formula.state;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.NOT_SYMBOL;

public class CTLNotFormula extends CTLFormula {
  
  private final CTLFormula argument;
  
  private CTLNotFormula(CTLFormula argument) {
    this.argument = argument;
  }
  
  public static CTLNotFormula of(CTLFormula argument) {
    return new CTLNotFormula( argument );
  }
  
  public CTLFormula getArgument() {
    return argument;
  }
  
  @Override
  public String toString() {
    return NOT_SYMBOL + argument.toString();
  }
}
