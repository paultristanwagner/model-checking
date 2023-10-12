package me.paultristanwagner.modelchecking.automaton;

import java.util.Objects;

public class CompositeState extends State {

  protected State left;
  protected Object right;

  public CompositeState(State left, Object right) {
    this.left = left;
    this.right = right;
  }

  public State getLeft() {
    return left;
  }

  public Object getRight() {
    return right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompositeState that = (CompositeState) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  public String toString() {
    return "<" + left + ", " + right + ">";
  }
}
