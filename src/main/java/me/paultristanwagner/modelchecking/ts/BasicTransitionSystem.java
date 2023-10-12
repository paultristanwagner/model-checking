package me.paultristanwagner.modelchecking.ts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.State;

public class BasicTransitionSystem extends TransitionSystem<String> {

  public BasicTransitionSystem(
      Set<State> states,
      Map<State, Set<State>> successors,
      Set<State> initialStates,
      Set<String> atomicPropositions,
      Map<State, Set<String>> labelingFunction) {
    super(states, successors, initialStates, atomicPropositions, labelingFunction);
  }

  public String introduceFreshAtomicProposition() {
    int i = 0;
    while (true) {
      String atomicProposition = "a_" + i;
      if (!atomicPropositions.contains(atomicProposition)) {
        atomicPropositions.add(atomicProposition);
        return atomicProposition;
      }
      i++;
    }
  }

  @Override
  public BasicTransitionSystem copy() {
    Set<State> states = new HashSet<>(this.states);
    Map<State, Set<State>> transitions = new HashMap<>(this.successors);
    Set<State> initialStates = new HashSet<>(this.initialStates);
    Set<String> atomicPropositions = new HashSet<>(this.atomicPropositions);

    Map<State, Set<String>> labelingFunction = new HashMap<>();
    this.labelingFunction.forEach(
        (state, labels) -> labelingFunction.put(state, new HashSet<>(labels)));

    return new BasicTransitionSystem(
        states, transitions, initialStates, atomicPropositions, labelingFunction);
  }
}
