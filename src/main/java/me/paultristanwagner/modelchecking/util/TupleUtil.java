package me.paultristanwagner.modelchecking.util;

public class TupleUtil {

  public static String stringTuple(Object... o) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < o.length; i++) {
      sb.append(o[i]);
      if (i < o.length - 1) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
