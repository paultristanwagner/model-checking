package me.paultristanwagner.modelchecking.automaton;

public abstract class State {

  public static BasicState named(String name) {
    return new BasicState(name);
  }

  public static CompositeState composite(State state, Object other) {
    return new CompositeState(state, other);
  }

  public static CompositeState composite(String name, Object other) {
    return composite(named(name), other);
  }
}
