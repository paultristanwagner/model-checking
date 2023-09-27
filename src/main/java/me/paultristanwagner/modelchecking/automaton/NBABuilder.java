package me.paultristanwagner.modelchecking.automaton;

import java.util.ArrayList;
import java.util.List;

public class NBABuilder {

  private final List<String> states;
  private final List<String> alphabet;
  private final List<String> initialStates;
  private final List<String> acceptingStates;
  private final List<NBATransition> transitions;

  public NBABuilder() {
    this.states = new ArrayList<>();
    this.alphabet = new ArrayList<>();
    this.initialStates = new ArrayList<>();
    this.acceptingStates = new ArrayList<>();
    this.transitions = new ArrayList<>();
  }

  public NBABuilder addStates(String... states) {
    for (String state : states) {
      addState(state);
    }

    return this;
  }

  public NBABuilder addState(String state) {
    this.states.add(state);
    return this;
  }

  public NBABuilder setAlphabet(List<String> alphabet) {
    this.alphabet.clear();
    this.alphabet.addAll(alphabet);
    return this;
  }

  public NBABuilder addInitialState(String initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public NBABuilder addAcceptingState(String acceptingState) {
    this.acceptingStates.add(acceptingState);
    return this;
  }

  public NBABuilder addTransition(String from, String action, String to) {
    this.transitions.add(NBATransition.of(from, action, to));
    return this;
  }

  public NBA build() {
    return new NBA(states, alphabet, initialStates, acceptingStates, transitions);
  }
}
