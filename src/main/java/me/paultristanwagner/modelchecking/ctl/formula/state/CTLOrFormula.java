package me.paultristanwagner.modelchecking.ctl.formula.state;

import static me.paultristanwagner.modelchecking.util.Symbol.OR_SYMBOL;

import java.util.Arrays;
import java.util.List;

public class CTLOrFormula extends CTLFormula {

  private final List<CTLFormula> components;

  private CTLOrFormula(List<CTLFormula> components) {
    this.components = components;
  }

  public static CTLOrFormula or(CTLFormula... components) {
    return new CTLOrFormula(Arrays.asList(components));
  }

  public static CTLOrFormula or(List<CTLFormula> components) {
    return new CTLOrFormula(components);
  }

  public List<CTLFormula> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < components.size(); i++) {
      builder.append(components.get(i).toString());

      if (i < components.size() - 1) {
        builder.append(" ");
        builder.append(OR_SYMBOL);
        builder.append(" ");
      }
    }

    return builder.toString();
  }
}
