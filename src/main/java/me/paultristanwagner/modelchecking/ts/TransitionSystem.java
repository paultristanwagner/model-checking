package me.paultristanwagner.modelchecking.ts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.automaton.NBATransition;
import me.paultristanwagner.modelchecking.ts.TSTransition.TSTransitionAdapter;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.notPersistent;
import static me.paultristanwagner.modelchecking.ts.TSPersistenceResult.persistent;
import static me.paultristanwagner.modelchecking.util.TupleUtil.stringTuple;

public class TransitionSystem {

  private static final Gson GSON;

  static {
    GSON = new GsonBuilder()
            .registerTypeAdapter(TSTransition.class, new TSTransitionAdapter())
            .setPrettyPrinting()
            .create();
  }

  private final List<String> states;
  private final List<TSTransition> transitions;
  private final List<String> initialStates;
  private final List<String> atomicPropositions;
  private final Map<String, List<String>> labelingFunction;

  public TransitionSystem(
      List<String> states,
      List<TSTransition> transitions,
      List<String> initialStates,
      List<String> atomicPropositions,
      Map<String, List<String>> labelingFunction) {
    this.states = states;
    this.transitions = transitions;
    this.initialStates = initialStates;
    this.atomicPropositions = atomicPropositions;
    this.labelingFunction = labelingFunction;

    for (String state : states) {
      labelingFunction.putIfAbsent(state, List.of());
    }
  }

  public TransitionSystem reachableSynchronousProduct(NBA nba) {
    TransitionSystemBuilder builder = new TransitionSystemBuilder();

    for (String state : nba.getStates()) {
      builder.addAtomicProposition(state);
    }

    Queue<SimpleEntry<String, String>> queue = new ArrayDeque<>();
    for (String initialState : initialStates) {
      List<String> label = labelingFunction.get(initialState);

      for(NBATransition nbaTransition : nba.getTransitions()) {
        String q0 = nbaTransition.getFrom();
        if(!nba.getInitialStates().contains(q0)) {
          continue;
        }

        String q = nbaTransition.getTo();

        // todo: match actions and labels more carefully
        if(!nbaTransition.getAction().equals(label.toString())) {
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
            List<String> sSuccessorLabel = labelingFunction.get(sSuccessor);
            String sSuccessorLabelString = sSuccessorLabel.toString();
            Set<String> qSuccessors = nba.getSuccessors(q, sSuccessorLabelString);
            for (String qSuccessor : qSuccessors) {

              String from = stringTuple(s, q);
              String to = stringTuple(sSuccessor, qSuccessor);

              builder.addTransition(from, to);

              SimpleEntry<String, String> successor = new SimpleEntry<>(sSuccessor, qSuccessor);
              if(!visited.contains(successor)) {
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
    while(!xi.isEmpty()) {
      String s1 = xi.peek();
      List<String> successors = getSuccessors(s1);
      if(successors.contains(s)) {
        xi.push(s);
        return true;
      } else if(!v.containsAll(successors)) {
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

    while(!u.containsAll(initialStates)) {
      Set<String> remaining = new HashSet<>(initialStates);
      remaining.removeAll(u);

      String s0 = remaining.stream().findAny().get();
      u.add(s0);

      pi.push(s0);

      while(!pi.isEmpty()) {
        String s = pi.peek();

        Set<String> remainingSuccessors = new HashSet<>(getSuccessors(s));
        remainingSuccessors.removeAll(u);

        if(!remainingSuccessors.isEmpty()) {
          String s1 = remainingSuccessors.stream().findAny().get();
          u.add(s1);
          pi.push(s1);
        } else {
          pi.pop();
          List<String> labels = labelingFunction.get(s);

          boolean notPersistentState = labels.stream().noneMatch(persistentStates::contains);

          if(notPersistentState && cycleCheck(s, v, xi)) {
            List<String> piList = new ArrayList<>(pi);
            List<String> xiList = new ArrayList<>(xi);

            InfinitePath witness = new InfinitePath(piList, xiList);
            return notPersistent(witness);
          }
        }
      }
    }

    return persistent();
  }

  @Override
  public String toString() {
    return toJson();
  }

  public List<String> getStates() {
    return states;
  }

  public List<String> getInitialStates() {
    return initialStates;
  }

  // todo: low hanging fruit: precompute successors
  public List<String> getSuccessors(String state) {
    return transitions.stream()
        .filter(transition -> transition.getFrom().equals(state))
        .map(TSTransition::getTo)
        .toList();
  }

  public List<String> getAtomicPropositions() {
    return atomicPropositions;
  }

  public List<String> getLabel(String state) {
    return labelingFunction.get(state);
  }

  public String toJson() {
    return GSON.toJson(this);
  }

  public static TransitionSystem fromJson(String string) {
    return GSON.fromJson( string, TransitionSystem.class );
  }
}
