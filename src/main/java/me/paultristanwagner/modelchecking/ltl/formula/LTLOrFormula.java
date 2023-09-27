package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.OR_SYMBOL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLOrFormula extends LTLFormula {

  private final List<LTLFormula> components;

  private LTLOrFormula(List<LTLFormula> components) {
    this.components = components;
  }

  public static LTLOrFormula or(LTLFormula... components) {
    return new LTLOrFormula(List.of(components));
  }

  public static LTLOrFormula or(List<LTLFormula> components) {
    return new LTLOrFormula(components);
  }

  @Override
  public List<LTLFormula> getAllSubformulas() {
    List<LTLFormula> subformulas = new ArrayList<>();
    subformulas.add(this);
    for (LTLFormula component : components) {
      subformulas.addAll(component.getAllSubformulas());
    }
    return subformulas;
  }

  public List<LTLFormula> getComponents() {
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

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LTLOrFormula orFormula && components.equals(orFormula.components);
  }

  @Override
  public int hashCode() {
    return Objects.hash(components);
  }
}
