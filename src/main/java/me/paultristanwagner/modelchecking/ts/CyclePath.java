package me.paultristanwagner.modelchecking.ts;

import static me.paultristanwagner.modelchecking.util.Symbol.LOWERCASE_OMEGA;

import java.util.ArrayList;
import java.util.List;

public record CyclePath(List<String> start, List<String> cycle) {

  private static final String TUPLE_PREFIX = "(";
  private static final String TUPLE_SUFFIX = ")";
  private static final String TUPLE_SEPARATOR = ",";

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String s : start) {
      sb.append(s).append(" ");
    }

    sb.append("(");
    for (int i = 0; i < cycle.size() - 1; i++) {
      String s = cycle.get(i);
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
    for (String s : start) {
      if (!s.startsWith(TUPLE_PREFIX) || !s.endsWith(TUPLE_SUFFIX)) {
        return this;
      }
    }

    for (String s : cycle) {
      if (!s.startsWith(TUPLE_PREFIX) || !s.endsWith(TUPLE_SUFFIX)) {
        return this;
      }
    }

    List<String> transformedStart = transformList(start);
    List<String> transformedCycle = transformList(cycle);

    return new CyclePath(transformedStart, transformedCycle);
  }

  private List<String> transformList(List<String> list) {
    List<String> transformed = new ArrayList<>();
    for (String s : list) {
      transformed.add(transformTuple(s));
    }

    return transformed;
  }

  private String transformTuple(String tupleString) {
    String removedPrefix = tupleString.substring(TUPLE_PREFIX.length());
    String removedSuffix =
        removedPrefix.substring(0, removedPrefix.length() - TUPLE_SUFFIX.length());
    String[] split = removedSuffix.split(TUPLE_SEPARATOR);

    return split[0];
  }
}
