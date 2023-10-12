package me.paultristanwagner.modelchecking.ts;

import static me.paultristanwagner.modelchecking.util.Symbol.LOWERCASE_OMEGA;

import java.util.ArrayList;
import java.util.List;
import me.paultristanwagner.modelchecking.automaton.CompositeState;
import me.paultristanwagner.modelchecking.automaton.State;

public record CyclePath(List<State> start, List<State> cycle) {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (State s : start) {
      sb.append(s).append(" ");
    }

    sb.append("(");
    for (int i = 0; i < cycle.size() - 1; i++) {
      State s = cycle.get(i);
      sb.append(s);

      if (i < cycle.size() - 2) {
        sb.append(" ");
      }
    }
    sb.append(")^");
    sb.append(LOWERCASE_OMEGA);

    return sb.toString();
  }

  public CyclePath reduce() {
    for (State s : start) {
      if (!(s instanceof CompositeState)) return this;
    }

    for (State s : cycle) {
      if (!(s instanceof CompositeState)) return this;
    }

    List<State> transformedStart = reduceList(start);
    List<State> transformedCycle = reduceList(cycle);

    return new CyclePath(transformedStart, transformedCycle);
  }

  private List<State> reduceList(List<State> list) {
    List<State> transformed = new ArrayList<>();
    for (State s : list) {
      if (!(s instanceof CompositeState compositeState)) {
        throw new IllegalStateException("Can only reduce composite states");
      }
      transformed.add(compositeState.getLeft());
    }

    return transformed;
  }
}
