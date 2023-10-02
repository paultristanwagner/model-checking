package me.paultristanwagner.modelchecking.automaton;

import java.util.HashSet;
import java.util.Set;

public class NBABuilder<ActionType> {

  private final Set<String> states;
  private final Set<String> alphabet;
  private final Set<String> initialStates;
  private final Set<String> acceptingStates;
  private final Set<NBATransition<ActionType>> transitions;

  public NBABuilder() {
    this.states = new HashSet<>();
    this.alphabet = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.acceptingStates = new HashSet<>();
    this.transitions = new HashSet<>();
  }

  public NBABuilder<ActionType> addStates(String... states) {
    for (String state : states) {
      addState(state);
    }

    return this;
  }

  public NBABuilder<ActionType> addState(String state) {
    this.states.add(state);
    return this;
  }

  public NBABuilder<ActionType> setAlphabet(Set<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public NBABuilder<ActionType> addInitialState(String initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public NBABuilder<ActionType> addAcceptingState(String acceptingState) {
    this.acceptingStates.add(acceptingState);
    return this;
  }

  public NBABuilder<ActionType> addTransition(String from, ActionType action, String to) {
    this.transitions.add(NBATransition.of(from, action, to));
    return this;
  }

  public NBA<ActionType> build() {
    return new NBA(states, alphabet, initialStates, acceptingStates, transitions);
  }
}
