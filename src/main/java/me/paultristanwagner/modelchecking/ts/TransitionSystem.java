package me.paultristanwagner.modelchecking.ts;

import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.notPersistent;
import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.persistent;
import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;
import static me.paultristanwagner.modelchecking.util.TupleUtil.stringTuple;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.automaton.NBATransition;

public class TransitionSystem {

  private final Set<String> states;
  private final Set<TSTransition> transitions;
  private final Set<String> initialStates;
  private final Set<String> atomicPropositions;
  private final Map<String, Set<String>> labelingFunction;

  public TransitionSystem(
      Set<String> states,
      Set<TSTransition> transitions,
      Set<String> initialStates,
      Set<String> atomicPropositions,
      Map<String, Set<String>> labelingFunction) {
    this.states = states;
    this.transitions = transitions;
    this.initialStates = initialStates;
    this.atomicPropositions = atomicPropositions;
    this.labelingFunction = labelingFunction;

    for (String state : states) {
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

  public TransitionSystem copy() {
    Set<String> states = new HashSet<>(this.states);
    Set<TSTransition> transitions = new HashSet<>(this.transitions);
    Set<String> initialStates = new HashSet<>(this.initialStates);
    Set<String> atomicPropositions = new HashSet<>(this.atomicPropositions);

    Map<String, Set<String>> labelingFunction = new HashMap<>();
    this.labelingFunction.forEach(
        (state, labels) -> labelingFunction.put(state, new HashSet<>(labels)));

    return new TransitionSystem(
        states, transitions, initialStates, atomicPropositions, labelingFunction);
  }

  public TransitionSystem reachableSynchronousProduct(NBA<Set<String>> nba) {
    TransitionSystemBuilder builder = new TransitionSystemBuilder();

    for (String state : nba.getStates()) {
      builder.addAtomicProposition(state);
    }

    Queue<SimpleEntry<String, String>> queue = new ArrayDeque<>();
    for (String initialState : initialStates) {
      Set<String> label = labelingFunction.get(initialState);

      for (NBATransition<Set<String>> nbaTransition : nba.getTransitions()) {
        String q0 = nbaTransition.getFrom();
        if (!nba.getInitialStates().contains(q0)) {
          continue;
        }

        String q = nbaTransition.getTo();

        if (!nbaTransition.getAction().equals(label)) {
          continue;
        }

        String resultState = stringTuple(initialState, q);
        builder.addState(resultState);
        builder.addLabel(resultState, q);
        builder.addInitialState(resultState);
        queue.add(new SimpleEntry<>(initialState, q));
      }
    }

    Set<SimpleEntry<String, String>> visited = new HashSet<>();

    while (!queue.isEmpty()) {
      SimpleEntry<String, String> state = queue.poll();
      visited.add(state);
      String s = state.getKey();

      String q = state.getValue();
      List<String> sSuccessors = getSuccessors(s);

      for (String sSuccessor : sSuccessors) {
        Set<String> sSuccessorLabel = labelingFunction.get(sSuccessor);
        Set<String> qSuccessors = nba.getSuccessors(q, sSuccessorLabel);
        for (String qSuccessor : qSuccessors) {

          String from = stringTuple(s, q);
          String to = stringTuple(sSuccessor, qSuccessor);

          builder.addTransition(from, to);

          SimpleEntry<String, String> successor = new SimpleEntry<>(sSuccessor, qSuccessor);
          if (!visited.contains(successor)) {
            queue.add(successor);
            builder.addState(to);
            builder.addLabel(to, qSuccessor);
          }
        }
      }
    }

    return builder.build();
  }

  private boolean cycleCheck(String s, Set<String> v, Stack<String> xi) {
    xi.push(s);
    v.add(s);
    while (!xi.isEmpty()) {
      String s1 = xi.peek();
      List<String> successors = getSuccessors(s1);
      if (successors.contains(s)) {
        xi.push(s);
        return true;
      } else if (!v.containsAll(successors)) {
        Set<String> remainingSuccessors = new HashSet<>(successors);
        remainingSuccessors.removeAll(v);

        String s2 = remainingSuccessors.stream().findAny().get();
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
  public TSPersistenceResult checkPersistence(Set<String> persistentStates) {
    // run a nested-depth-first-search
    Set<String> u = new HashSet<>();
    Set<String> v = new HashSet<>();

    Stack<String> pi = new Stack<>();
    Stack<String> xi = new Stack<>();

    while (!u.containsAll(initialStates)) {
      Set<String> remaining = new HashSet<>(initialStates);
      remaining.removeAll(u);

      String s0 = remaining.stream().findAny().get();
      u.add(s0);

      pi.push(s0);

      while (!pi.isEmpty()) {
        String s = pi.peek();

        Set<String> remainingSuccessors = new HashSet<>(getSuccessors(s));
        remainingSuccessors.removeAll(u);

        if (!remainingSuccessors.isEmpty()) {
          String s1 = remainingSuccessors.stream().findAny().get();
          u.add(s1);
          pi.push(s1);
        } else {
          pi.pop();
          Set<String> labels = labelingFunction.get(s);

          boolean notPersistentState = labels.stream().noneMatch(persistentStates::contains);

          if (notPersistentState && cycleCheck(s, v, xi)) {
            List<String> piList = new ArrayList<>(pi);
            List<String> xiList = new ArrayList<>(xi);

            CyclePath witness = new CyclePath(piList, xiList);
            return notPersistent(witness);
          }
        }
      }
    }

    return persistent();
  }

  public void addAtomicProposition(String atomicProposition) {
    atomicPropositions.add(atomicProposition);
  }

  public void removeAtomicProposition(String atomicProposition) {
    atomicPropositions.remove(atomicProposition);
    labelingFunction.forEach((state, labels) -> labels.remove(atomicProposition));
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

  public void clearInitialStates() {
    initialStates.clear();
  }

  public void addInitialState(String state) {
    initialStates.add(state);
  }

  @Override
  public String toString() {
    return toJson();
  }

  public Set<String> getStates() {
    return states;
  }

  public Set<String> getInitialStates() {
    return initialStates;
  }

  // todo: low hanging fruit: precompute successors
  public List<String> getSuccessors(String state) {
    return transitions.stream()
        .filter(transition -> transition.getFrom().equals(state))
        .map(TSTransition::getTo)
        .toList();
  }

  public Set<String> getAtomicPropositions() {
    return atomicPropositions;
  }

  public void addLabel(String state, String atomicProposition) {
    labelingFunction.get(state).add(atomicProposition);
  }

  public Set<String> getLabel(String state) {
    return labelingFunction.get(state);
  }

  public String toJson() {
    return GSON.toJson(this);
  }
}
