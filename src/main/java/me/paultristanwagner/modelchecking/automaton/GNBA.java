package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;
import static me.paultristanwagner.modelchecking.util.TupleUtil.stringTuple;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class GNBA<ActionType> {

  private final Set<String> states;
  private final Set<String> alphabet;
  private final Set<String> initialStates;
  private final Set<Set<String>> acceptingSets;
  private final Set<NBATransition<ActionType>> transitions;

  public GNBA(
      Set<String> states,
      Set<String> alphabet,
      Set<String> initialStates,
      Set<Set<String>> acceptingSets,
      Set<NBATransition<ActionType>> transitions) {
    this.states = states;
    this.alphabet = alphabet;
    this.initialStates = initialStates;
    this.acceptingSets = acceptingSets;
    this.transitions = transitions;
  }

  public Set<String> getSuccessors(String state) {
    Set<String> successors = new HashSet<>();
    for (NBATransition<ActionType> transition : transitions) {
      if (transition.getFrom().equals(state)) {
        successors.add(transition.getTo());
      }
    }
    return successors;
  }

  public Set<String> getSuccessors(String state, String action) {
    Set<String> successors = new HashSet<>();
    for (NBATransition<ActionType> transition : transitions) {
      if (transition.getFrom().equals(state) && transition.getAction().equals(action)) {
        successors.add(transition.getTo());
      }
    }
    return successors;
  }

  public NBA<ActionType> convertToNBA() {
    if (acceptingSets.size() <= 1) {
      return getCanonicalNBA();
    }

    NBABuilder<ActionType> builder = new NBABuilder<>();
    builder.setAlphabet(alphabet);

    for (String state : states) {
      for (int i = 0; i < acceptingSets.size(); i++) {
        String nbaState = stringTuple(state, i + 1);
        builder.addState(nbaState);
      }
    }

    for (String initialState : initialStates) {
      String nbaInitialState = stringTuple(initialState, 1);
      builder.addInitialState(nbaInitialState);
    }

    if (!acceptingSets.isEmpty()) {
      Set<String> acceptingSet = acceptingSets.stream().findFirst().get();
      for (String acceptingState : acceptingSet) {
        String nbaAcceptingState = stringTuple(acceptingState, 1);
        builder.addAcceptingState(nbaAcceptingState);
      }
    }

    for (NBATransition<ActionType> transition : transitions) {
      int i = 0;
      for (Set<String> acceptingSet : acceptingSets) {
        String nbaFrom = stringTuple(transition.getFrom(), i + 1);
        String nbaTo;
        if (acceptingSet.contains(transition.getFrom())) {
          nbaTo = stringTuple(transition.getTo(), ((i + 1) % acceptingSets.size() + 1));
        } else {
          nbaTo = stringTuple(transition.getTo(), (i + 1));
        }
        builder.addTransition(nbaFrom, transition.getAction(), nbaTo);

        i++;
      }
    }

    return builder.build();
  }

  private NBA<ActionType> getCanonicalNBA() {
    if (acceptingSets.size() > 1) {
      throw new IllegalStateException("GNBA has more than one accepting set");
    }

    NBABuilder<ActionType> builder = new NBABuilder<>();
    builder.setAlphabet(alphabet);
    for (String state : states) {
      builder.addState(state);
    }

    for (String initialState : initialStates) {
      builder.addInitialState(initialState);
    }

    if (acceptingSets.isEmpty()) {
      for (String state : states) {
        builder.addAcceptingState(state);
      }
    } else {
      for (String acceptingState : acceptingSets.stream().findFirst().get()) {
        builder.addAcceptingState(acceptingState);
      }
    }

    for (NBATransition<ActionType> transition : transitions) {
      builder.addTransition(transition.getFrom(), transition.getAction(), transition.getTo());
    }

    return builder.build();
  }

  public String toJson() {
    return GSON.toJson(this);
  }

  public static <ActionType> GNBA<ActionType> fromJson(String json, Type type) {
    return GSON.fromJson(json, type);
  }

  public Set<String> getStates() {
    return states;
  }

  public Set<String> getInitialStates() {
    return initialStates;
  }

  public Set<String> getAlphabet() {
    return alphabet;
  }

  public Set<Set<String>> getAcceptingSets() {
    return acceptingSets;
  }

  public Set<NBATransition<ActionType>> getTransitions() {
    return transitions;
  }
}
