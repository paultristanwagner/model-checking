package me.paultristanwagner.modelchecking.ctlstar.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.OR_SYMBOL;

import java.util.*;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.formula.LTLOrFormula;

public class CTLStarOrFormula extends CTLStarFormula {

  private final List<CTLStarFormula> components;

  private CTLStarOrFormula( List<CTLStarFormula> components ) {
    this.components = components;
  }

  public static CTLStarOrFormula or( List<CTLStarFormula> components ) {
    return new CTLStarOrFormula( components );
  }

  public static CTLStarOrFormula or( CTLStarFormula... components ) {
    return new CTLStarOrFormula( List.of( components ) );
  }

  @Override
  public int getDepth() {
    int maxDepth = 0;
    for (CTLStarFormula component : components) {
      maxDepth = Math.max(maxDepth, component.getDepth());
    }
    return maxDepth + 1;
  }

  @Override
  public void replaceFormula(CTLStarFormula target, String freshVariable) {
    components.replaceAll(
        component -> {
          if (component.equals(target)) {
            return CTLStarIdentifierFormula.identifier(freshVariable);
          } else {
            component.replaceFormula(target, freshVariable);
            return component;
          }
        });
  }

  @Override
  public Set<CTLStarFormula> getSubFormulas() {
    Set<CTLStarFormula> subFormulas = new HashSet<>();
    for (CTLStarFormula component : components) {
      subFormulas.addAll(component.getSubFormulas());
    }
    subFormulas.add(this);
    return subFormulas;
  }

  @Override
  public LTLFormula toLTL() {
    List<LTLFormula> ltlComponents = new ArrayList<>();
    for (CTLStarFormula component : components) {
      ltlComponents.add(component.toLTL());
    }
    return LTLOrFormula.or(ltlComponents);
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
        builder.append(OR_SYMBOL);
        builder.append(" ");
      }
    }

    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CTLStarOrFormula that = (CTLStarOrFormula) o;
    return Objects.equals(components, that.components);
  }

  @Override
  public int hashCode() {
    return Objects.hash(components);
  }
}
