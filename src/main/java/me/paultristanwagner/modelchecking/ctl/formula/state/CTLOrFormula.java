package me.paultristanwagner.modelchecking.ctl.formula.state;

import java.util.Arrays;
import java.util.List;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.OR_SYMBOL;

public class CTLOrFormula extends CTLFormula {

  private final List<CTLFormula> components;
  
  private CTLOrFormula(List<CTLFormula> components) {
    this.components = components;
  }
  
  public static CTLOrFormula of(CTLFormula... components) {
    return new CTLOrFormula( Arrays.asList( components ) );
  }
  
  public static CTLOrFormula of(List<CTLFormula> components) {
    return new CTLOrFormula( components );
  }
  
  public List<CTLFormula> getComponents() {
    return components;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < components.size(); i++) {
      builder.append( components.get( i ).toString() );
      
      if (i < components.size() - 1) {
        builder.append( " " );
        builder.append( OR_SYMBOL );
        builder.append( " " );
      }
    }
    
    return builder.toString();
  }
}
