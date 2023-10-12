package me.paultristanwagner.modelchecking.ts;

import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.notPersistent;
import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.persistent;
import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;

import java.util.*;
import java.util.List;
import me.paultristanwagner.modelchecking.automaton.CompositeState;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.automaton.NBATransition;
import me.paultristanwagner.modelchecking.automaton.State;

public class TransitionSystem<APType> {

  protected final Set<State> states;
  protected final Map<State, Set<State>> successors;
  protected final Set<State> initialStates;
  protected final Set<APType> atomicPropositions;
  protected final Map<State, Set<APType>> labelingFunction;

  protected TransitionSystem() {
    this.states = new HashSet<>();
    this.successors = new HashMap<>();
    this.initialStates = new HashSet<>();
    this.atomicPropositions = new HashSet<>();
    this.labelingFunction = new HashMap<>();
  }

  public TransitionSystem(
      Set<State> states,
      Map<State, Set<State>> successors,
      Set<State> initialStates,
      Set<APType> atomicPropositions,
      Map<State, Set<APType>> labelingFunction) {
    this.states = states;
    this.successors = successors;
    this.initialStates = initialStates;
    this.atomicPropositions = atomicPropositions;
    this.labelingFunction = labelingFunction;

    for (State state : states) {
      labelingFunction.putIfAbsent(state, Set.of());
    }
  }

  public void verifyNoNullLabels() {
    labelingFunction.forEach(
        (state, labels) -> {
          if (labels.contains(null)) {
            throw new IllegalStateException("Null label for state " + state);
          }
        });
  }

  public TransitionSystem<APType> copy() {
    Set<State> states = new HashSet<>(this.states);
    Map<State, Set<State>> successors = new HashMap<>(this.successors);
    Set<State> initialStates = new HashSet<>(this.initialStates);
    Set<APType> atomicPropositions = new HashSet<>(this.atomicPropositions);

    Map<State, Set<APType>> labelingFunction = new HashMap<>();
    this.labelingFunction.forEach(
        (state, labels) -> labelingFunction.put(state, new HashSet<>(labels)));

    return new TransitionSystem<>(
        states, successors, initialStates, atomicPropositions, labelingFunction);
  }

  public TransitionSystem<State> reachableSynchronousProduct(NBA<Set<APType>> nba) {
    TransitionSystemBuilder<State> builder = new TransitionSystemBuilder<>();

    for (State state : nba.getStates()) {
      builder.addAtomicProposition(state);
    }

    Queue<CompositeState> queue = new ArrayDeque<>();
    for (State initialState : initialStates) {
      Set<APType> label = labelingFunction.get(initialState);

      for (NBATransition<Set<APType>> nbaTransition :
          nba.getTransitionFunction().getTransitions()) {
        State q0 = nbaTransition.getFrom();
        if (!nba.getInitialStates().contains(q0)) {
          continue;
        }

        State q = nbaTransition.getTo();

        if (!nbaTransition.getAction().equals(label)) {
          continue;
        }

        CompositeState resultState = State.composite(initialState, q);
        builder.addState(resultState);
        builder.addLabel(resultState, q);
        builder.addInitialState(resultState);
        queue.add(resultState);
      }
    }

    Set<CompositeState> visited = new HashSet<>();

    while (!queue.isEmpty()) {
      CompositeState state = queue.poll();
      visited.add(state);
      State s = state.getLeft();

      State q = (State) state.getRight();
      Set<State> sSuccessors = getSuccessors(s);

      for (State sSuccessor : sSuccessors) {
        Set<APType> sSuccessorLabel = labelingFunction.get(sSuccessor);
        Set<State> qSuccessors = nba.getSuccessors(q, sSuccessorLabel);
        for (State qSuccessor : qSuccessors) {

          CompositeState from = State.composite(s, q);
          CompositeState to = State.composite(sSuccessor, qSuccessor);

          builder.addTransition(from, to);

          if (!visited.contains(to)) {
            queue.add(to);
            builder.addState(to);
            builder.addLabel(to, qSuccessor);
          }
        }
      }
    }

    return builder.build();
  }

  private boolean cycleCheck(State s, Set<State> v, Stack<State> xi) {
    xi.push(s);
    v.add(s);
    while (!xi.isEmpty()) {
      State s1 = xi.peek();
      Set<State> successors = getSuccessors(s1);
      if (successors.contains(s)) {
        xi.push(s);
        return true;
      } else if (!v.containsAll(successors)) {
        Set<State> remainingSuccessors = new HashSet<>(successors);
        remainingSuccessors.removeAll(v);

        State s2 = remainingSuccessors.stream().findAny().get();
        v.add(s2);
        xi.push(s2);
      } else {
        xi.pop();
      }
    }

    return false;
  }

  /**
   * @return whether the ts satisfies the persistence property 'eventually always P'
   */
  public TSPersistenceResult checkPersistence(Set<State> persistentStates) {
    // run a nested-depth-first-search
    Set<State> u = new HashSet<>();
    Set<State> v = new HashSet<>();

    Stack<State> pi = new Stack<>();
    Stack<State> xi = new Stack<>();

    while (!u.containsAll(initialStates)) {
      Set<State> remaining = new HashSet<>(initialStates);
      remaining.removeAll(u);

      State s0 = remaining.stream().findAny().get();
      u.add(s0);

      pi.push(s0);

      while (!pi.isEmpty()) {
        State s = pi.peek();

        Set<State> remainingSuccessors = new HashSet<>(getSuccessors(s));
        remainingSuccessors.removeAll(u);

        if (!remainingSuccessors.isEmpty()) {
          State s1 = remainingSuccessors.stream().findAny().get();
          u.add(s1);
          pi.push(s1);
        } else {
          pi.pop();
          Set<APType> labels = labelingFunction.get(s);

          boolean notPersistentState = labels.stream().noneMatch(persistentStates::contains);

          if (notPersistentState && cycleCheck(s, v, xi)) {
            List<State> piList = new ArrayList<>(pi);
            List<State> xiList = new ArrayList<>(xi);

            CyclePath witness = new CyclePath(piList, xiList);
            return notPersistent(witness);
          }
        }
      }
    }

    return persistent();
  }

  public void addAtomicProposition(APType atomicProposition) {
    atomicPropositions.add(atomicProposition);
  }

  public void removeAtomicProposition(APType atomicProposition) {
    atomicPropositions.remove(atomicProposition);
    labelingFunction.forEach((state, labels) -> labels.remove(atomicProposition));
  }

  public void clearInitialStates() {
    initialStates.clear();
  }

  public void addInitialState(State state) {
    initialStates.add(state);
  }

  @Override
  public String toString() {
    return toJson();
  }

  public Set<State> getStates() {
    return states;
  }

  public Set<State> getInitialStates() {
    return initialStates;
  }

  public Set<State> getSuccessors(State state) {
    return successors.getOrDefault(state, new HashSet<>());
  }

  public Set<APType> getAtomicPropositions() {
    return atomicPropositions;
  }

  public void addLabel(State state, APType atomicProposition) {
    labelingFunction.get(state).add(atomicProposition);
  }

  public Set<APType> getLabel(State state) {
    return labelingFunction.get(state);
  }

  public String toJson() {
    return GSON.toJson(this);
  }
}
