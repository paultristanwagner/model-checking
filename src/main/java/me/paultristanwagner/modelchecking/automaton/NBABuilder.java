package me.paultristanwagner.modelchecking.automaton;

import java.util.HashSet;
import java.util.Set;

public class NBABuilder<ActionType> {

  private final Set<State> states;
  private final Set<String> alphabet;
  private final Set<State> initialStates;
  private final Set<State> acceptingStates;
  private final TransitionFunction<ActionType> transitionFunction;

  public NBABuilder() {
    this.states = new HashSet<>();
    this.alphabet = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.acceptingStates = new HashSet<>();
    this.transitionFunction = new TransitionFunction<>();
  }

  public NBABuilder<ActionType> addStates(State... states) {
    for (State state : states) {
      addState(state);
    }

    return this;
  }

  public NBABuilder<ActionType> addState(State state) {
    this.states.add(state);
    return this;
  }

  public NBABuilder<ActionType> addState(String name) {
    return this.addState(State.named(name));
  }

  public NBABuilder<ActionType> setAlphabet(Set<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public NBABuilder<ActionType> addInitialState(State initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public NBABuilder<ActionType> addInitialState(String name) {
    return this.addInitialState(State.named(name));
  }

  public NBABuilder<ActionType> addAcceptingState(State acceptingState) {
    this.acceptingStates.add(acceptingState);
    return this;
  }

  public NBABuilder<ActionType> addAcceptingState(String name) {
    return this.addAcceptingState(State.named(name));
  }

  public NBABuilder<ActionType> addTransition(State from, ActionType action, State to) {
    this.transitionFunction.addTransition(from, action, to);
    return this;
  }

  public NBABuilder<ActionType> addTransition(String fromName, ActionType action, String toName) {
    return this.addTransition(State.named(fromName), action, State.named(toName));
  }

  public NBA<ActionType> build() {
    return new NBA<>(states, alphabet, initialStates, acceptingStates, transitionFunction);
  }
}
