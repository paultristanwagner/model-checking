package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.AND_SYMBOL;

import java.util.Arrays;
import java.util.List;

public class CTLStarAndFormula extends CTLStarFormula {

  private final List<CTLStarFormula> components;

  private CTLStarAndFormula(List<CTLStarFormula> components) {
    this.components = components;
  }

  public static CTLStarAndFormula and(List<CTLStarFormula> components) {
    return new CTLStarAndFormula(components);
  }

  public static CTLStarAndFormula and(CTLStarFormula... components) {
    return new CTLStarAndFormula(Arrays.asList(components));
  }

  public List<CTLStarFormula> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < components.size(); i++) {
      builder.append(components.get(i).toString());

      if (i < components.size() - 1) {
        builder.append(" ");
        builder.append(AND_SYMBOL);
        builder.append(" ");
      }
    }

    return builder.toString();
  }
}
