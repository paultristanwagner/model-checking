package me.paultristanwagner.modelchecking.automaton;

import java.util.HashSet;
import java.util.Set;

public class GNBABuilder<ActionType> {

  private final Set<String> states;
  private final Set<String> alphabet;
  private final Set<String> initialStates;
  private final Set<Set<String>> acceptingSets;
  private final Set<NBATransition<ActionType>> transitions;

  public GNBABuilder() {
    this.states = new HashSet<>();
    this.alphabet = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.acceptingSets = new HashSet<>();
    this.transitions = new HashSet<>();
  }

  public GNBABuilder<ActionType> addStates(String... states) {
    for (String state : states) {
      addState(state);
    }

    return this;
  }

  public GNBABuilder<ActionType> addState(String state) {
    this.states.add(state);
    return this;
  }

  public GNBABuilder<ActionType> setAlphabet(Set<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public GNBABuilder<ActionType> addInitialState(String initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public GNBABuilder<ActionType> addAcceptingSet(Set<String> acceptingSet) {
    this.acceptingSets.add(new HashSet<>(acceptingSet));
    return this;
  }

  public GNBABuilder<ActionType> addTransition(String from, ActionType action, String to) {
    this.transitions.add(NBATransition.of(from, action, to));
    return this;
  }

  public GNBA<ActionType> build() {
    return new GNBA<>(states, alphabet, initialStates, acceptingSets, transitions);
  }
}
