package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;

import java.lang.reflect.Type;
import java.util.Set;

public class GNBA<ActionType> {

  private final Set<State> states;
  private final Set<String> alphabet;
  private final Set<State> initialStates;
  private final Set<Set<State>> acceptingSets;
  private final TransitionFunction<ActionType> transitionFunction;

  public GNBA(
      Set<State> states,
      Set<String> alphabet,
      Set<State> initialStates,
      Set<Set<State>> acceptingSets,
      TransitionFunction<ActionType> transitionFunction) {
    this.states = states;
    this.alphabet = alphabet;
    this.initialStates = initialStates;
    this.acceptingSets = acceptingSets;
    this.transitionFunction = transitionFunction;
  }

  public Set<State> getSuccessors(State state) {
    return transitionFunction.getSuccessors(state);
  }

  public Set<State> getSuccessors(State state, ActionType action) {
    return transitionFunction.getSuccessors(state, action);
  }

  public NBA<ActionType> convertToNBA() {
    if (acceptingSets.size() <= 1) {
      return getCanonicalNBA();
    }

    NBABuilder<ActionType> builder = new NBABuilder<>();
    builder.setAlphabet(alphabet);

    for (State state : states) {
      for (int i = 0; i < acceptingSets.size(); i++) {
        State nbaState = State.composite(state, i + 1);
        builder.addState(nbaState);
      }
    }

    for (State initialState : initialStates) {
      State nbaInitialState = State.composite(initialState, 1);
      builder.addInitialState(nbaInitialState);
    }

    if (!acceptingSets.isEmpty()) {
      Set<State> acceptingSet = acceptingSets.stream().findFirst().get();
      for (State acceptingState : acceptingSet) {
        State nbaAcceptingState = State.composite(acceptingState, 1);
        builder.addAcceptingState(nbaAcceptingState);
      }
    }

    for (NBATransition<ActionType> transition : transitionFunction.getTransitions()) {
      int i = 0;
      for (Set<State> acceptingSet : acceptingSets) {
        State nbaFrom = State.composite(transition.getFrom(), i + 1);
        State nbaTo;
        if (acceptingSet.contains(transition.getFrom())) {
          nbaTo = State.composite(transition.getTo(), (i + 1) % acceptingSets.size() + 1);
        } else {
          nbaTo = State.composite(transition.getTo(), i + 1);
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
    for (State state : states) {
      builder.addState(state);
    }

    for (State initialState : initialStates) {
      builder.addInitialState(initialState);
    }

    if (acceptingSets.isEmpty()) {
      for (State state : states) {
        builder.addAcceptingState(state);
      }
    } else {
      for (State acceptingState : acceptingSets.stream().findFirst().get()) {
        builder.addAcceptingState(acceptingState);
      }
    }

    for (NBATransition<ActionType> transition : transitionFunction.getTransitions()) {
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

  public Set<State> getStates() {
    return states;
  }

  public Set<State> getInitialStates() {
    return initialStates;
  }

  public Set<String> getAlphabet() {
    return alphabet;
  }

  public Set<Set<State>> getAcceptingSets() {
    return acceptingSets;
  }

  public TransitionFunction<ActionType> getTransitionFunction() {
    return transitionFunction;
  }
}
