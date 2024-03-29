package me.paultristanwagner.modelchecking.ctl.formula.state;

import static me.paultristanwagner.modelchecking.util.Symbol.AND;

import java.util.Arrays;
import java.util.List;

public class CTLAndFormula extends CTLFormula {

  private final List<CTLFormula> components;

  private CTLAndFormula(List<CTLFormula> components) {
    this.components = components;
  }

  public static CTLAndFormula and(CTLFormula... components) {
    return new CTLAndFormula(Arrays.asList(components));
  }

  public static CTLAndFormula and(List<CTLFormula> components) {
    return new CTLAndFormula(components);
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
        builder.append(AND);
        builder.append(" ");
      }
    }

    return builder.toString();
  }
}
