package me.paultristanwagner.modelchecking.ts;

import java.util.*;
import me.paultristanwagner.modelchecking.automaton.State;

public class TransitionSystemBuilder<APType> {

  private final Set<State> states;
  private final Map<State, Set<State>> successors;
  private final Set<State> initialStates;
  private final Set<APType> atomicPropositions;
  private final Map<State, Set<APType>> labelingFunction;

  public TransitionSystemBuilder() {
    this.states = new HashSet<>();
    this.successors = new HashMap<>();
    this.initialStates = new HashSet<>();
    this.atomicPropositions = new HashSet<>();
    this.labelingFunction = new HashMap<>();
  }

  public TransitionSystemBuilder<APType> addStates(State... states) {
    for (State state : states) {
      addState(state);
    }

    return this;
  }

  public TransitionSystemBuilder<APType> addState(State state) {
    this.states.add(state);
    return this;
  }

  public TransitionSystemBuilder<APType> addTransition(State from, State to) {
    this.successors.compute(
        from,
        (k, v) -> {
          if (v == null) {
            Set<State> successors = new HashSet<>();
            successors.add(to);
            return successors;
          } else {
            v.add(to);
            return v;
          }
        });
    return this;
  }

  public TransitionSystemBuilder<APType> addInitialState(State initialState) {
    this.initialStates.add(initialState);
    return this;
  }

  public final TransitionSystemBuilder<APType> setAtomicPropositions(APType... atomicPropositions) {
    return setAtomicPropositions(Arrays.asList(atomicPropositions));
  }

  public TransitionSystemBuilder<APType> setAtomicPropositions(List<APType> atomicPropositions) {
    this.atomicPropositions.clear();
    this.atomicPropositions.addAll(atomicPropositions);
    return this;
  }

  public TransitionSystemBuilder<APType> addAtomicProposition(APType atomicProposition) {
    this.atomicPropositions.add(atomicProposition);
    return this;
  }

  public TransitionSystemBuilder<APType> addLabel(State state, APType atomicProposition) {
    Set<APType> label = labelingFunction.computeIfAbsent(state, k -> new HashSet<>());
    label.add(atomicProposition);

    return this;
  }

  public TransitionSystemBuilder<APType> addLabels(State state, APType... atomicPropositions) {
    Set<APType> labels = labelingFunction.computeIfAbsent(state, k -> new HashSet<>());
    labels.addAll(Arrays.asList(atomicPropositions));

    return this;
  }

  public TransitionSystem<APType> build() {
    return new TransitionSystem<>(
        states, successors, initialStates, atomicPropositions, labelingFunction);
  }

  public Set<State> getInitialStates() {
    return initialStates;
  }
}
