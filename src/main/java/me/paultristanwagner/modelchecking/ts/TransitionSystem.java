package me.paultristanwagner.modelchecking.ts;

import com.google.gson.*;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.automaton.NBATransition;
import me.paultristanwagner.modelchecking.ts.TSTransition.TSTransitionAdapter;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

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

        if(!nbaTransition.getAction().equals(label.toString())) {
          continue;
        }

        String resultState = "(" + initialState + "," + q + ")";
        builder.addState(resultState);
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

              String from = "(" + s + "," + q + ")";
              String to = "(" + sSuccessor + "," + qSuccessor + ")";

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
