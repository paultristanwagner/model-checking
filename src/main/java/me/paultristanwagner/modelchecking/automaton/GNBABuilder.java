package me.paultristanwagner.modelchecking.automaton;

import java.util.HashSet;
import java.util.Set;

public class GNBABuilder<ActionType> {

  private final Set<State> states;
  private final Set<String> alphabet;
  private final Set<State> initialStates;
  private final Set<Set<State>> acceptingSets;
  private final TransitionFunction<ActionType> transitionFunction;

  public GNBABuilder() {
    this.states = new HashSet<>();
    this.alphabet = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.acceptingSets = new HashSet<>();
    this.transitionFunction = new TransitionFunction<>();
  }

  public GNBABuilder<ActionType> addStates(State... states) {
    for (State state : states) {
      addState(state);
    }

    return this;
  }

  public GNBABuilder<ActionType> addState(State state) {
    this.states.add(state);
    return this;
  }

  public GNBABuilder<ActionType> addState(String name) {
    return this.addState(State.named(name));
  }

  public GNBABuilder<ActionType> setAlphabet(Set<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public GNBABuilder<ActionType> addInitialState(State initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public GNBABuilder<ActionType> addInitialState(String name) {
    return this.addInitialState(State.named(name));
  }

  public GNBABuilder<ActionType> addAcceptingSet(Set<State> acceptingSet) {
    this.acceptingSets.add(new HashSet<>(acceptingSet));
    return this;
  }

  public GNBABuilder<ActionType> addTransition(State from, ActionType action, State to) {
    this.transitionFunction.addTransition(from, action, to);
    return this;
  }

  public GNBABuilder<ActionType> addTransition(String fromName, ActionType action, String toName) {
    return this.addTransition(State.named(fromName), action, State.named(toName));
  }

  public GNBA<ActionType> build() {
    return new GNBA<>(states, alphabet, initialStates, acceptingSets, transitionFunction);
  }
}
