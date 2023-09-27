package me.paultristanwagner.modelchecking.automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GNBABuilder {

  private final List<String> states;
  private final List<String> alphabet;
  private final List<String> initialStates;
  private final List<List<String>> acceptingSets;
  private final List<NBATransition> transitions;

  public GNBABuilder() {
    this.states = new ArrayList<>();
    this.alphabet = new ArrayList<>();
    this.initialStates = new ArrayList<>();
    this.acceptingSets = new ArrayList<>();
    this.transitions = new ArrayList<>();
  }

  public GNBABuilder addStates(String... states) {
    for (String state : states) {
      addState(state);
    }

    return this;
  }

  public GNBABuilder addState(String state) {
    this.states.add(state);
    return this;
  }

  public GNBABuilder setAlphabet(List<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public GNBABuilder addInitialState(String initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public GNBABuilder addAcceptingSet(String... acceptingSet) {
    this.acceptingSets.add(List.of(acceptingSet));
    return this;
  }

  public GNBABuilder addAcceptingSet(Set<String> acceptingSet) {
    this.acceptingSets.add(new ArrayList<>(acceptingSet));
    return this;
  }

  public GNBABuilder addTransition(String from, String action, String to) {
    this.transitions.add(NBATransition.of(from, action, to));
    return this;
  }

  public GNBA build() {
    return new GNBA(states, alphabet, initialStates, acceptingSets, transitions);
  }
}
