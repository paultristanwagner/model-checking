package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;

import java.lang.reflect.Type;
import java.util.*;
import me.paultristanwagner.modelchecking.ts.CyclePath;

public class NBA<ActionType> {

  private final Set<State> states;
  private final Set<String> alphabet;
  private final Set<State> initialStates;
  private final Set<State> acceptingStates;
  private final TransitionFunction<ActionType> transitionFunction;

  public NBA(
      Set<State> states,
      Set<String> alphabet,
      Set<State> initialStates,
      Set<State> acceptingStates,
      TransitionFunction<ActionType> transitionFunction) {
    this.states = states;
    this.alphabet = alphabet;
    this.initialStates = initialStates;
    this.acceptingStates = acceptingStates;
    this.transitionFunction = transitionFunction;
  }

  public Set<State> getSuccessors(State state) {
    return transitionFunction.getSuccessors(state);
  }

  public Set<State> getSuccessors(State state, ActionType action) {
    return transitionFunction.getSuccessors(state, action);
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

  public NBAEmptinessResult checkEmptiness() {
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
          if (acceptingStates.contains(s) && cycleCheck(s, v, xi)) {
            List<State> piList = new ArrayList<>(pi);
            List<State> xiList = new ArrayList<>(xi);

            CyclePath witness = new CyclePath(piList, xiList);
            return NBAEmptinessResult.nonEmpty(witness);
          }
        }
      }
    }

    return NBAEmptinessResult.empty();
  }

  public GNBA<ActionType> toGNBA() {
    Set<State> states = new HashSet<>(this.states);
    Set<String> alphabet = new HashSet<>(this.alphabet);
    Set<State> initialStates = new HashSet<>(this.initialStates);
    TransitionFunction<ActionType> transitionFunction =
        new TransitionFunction<>(this.transitionFunction);

    Set<Set<State>> acceptingSets = new HashSet<>();
    acceptingSets.add(new HashSet<>(this.acceptingStates));

    return new GNBA<>(states, alphabet, initialStates, acceptingSets, transitionFunction);
  }

  public GNBA<ActionType> product(NBA<ActionType> other) {
    GNBABuilder<ActionType> builder = new GNBABuilder<>();
    builder.setAlphabet(alphabet);

    for (State state1 : states) {
      for (State state2 : other.states) {
        State state = State.composite(state1, state2);
        builder.addState(state);
      }
    }

    for (State initialState : initialStates) {
      for (State otherInitialState : other.initialStates) {
        State state = State.composite(initialState, otherInitialState);
        builder.addInitialState(state);
      }
    }

    Set<ActionType> actions = new HashSet<>();
    actions.addAll(transitionFunction.getActions());
    actions.addAll(other.transitionFunction.getActions());
    for (ActionType action : actions) {
      for (NBATransition<ActionType> transition : transitionFunction.getTransitions(action)) {
        for (NBATransition<ActionType> otherTransition :
            other.transitionFunction.getTransitions(action)) {
          State from = State.composite(transition.getFrom(), otherTransition.getFrom());
          State to = State.composite(transition.getTo(), otherTransition.getTo());
          builder.addTransition(from, action, to);
        }
      }
    }

    Set<State> acceptingSet1 = new HashSet<>();
    for (State acceptingState : acceptingStates) {
      for (State state : other.states) {
        State newAcceptingState = State.composite(acceptingState, state);
        acceptingSet1.add(newAcceptingState);
      }
    }

    Set<State> acceptingSet2 = new HashSet<>();
    for (State acceptingState : other.acceptingStates) {
      for (State state : states) {
        State newAcceptingState = State.composite(state, acceptingState);
        acceptingSet2.add(newAcceptingState);
      }
    }

    builder.addAcceptingSet(acceptingSet1);
    builder.addAcceptingSet(acceptingSet2);

    return builder.build();
  }

  public String toJson() {
    return GSON.toJson(this);
  }

  public static <ActionType> NBA<ActionType> fromJson(String json, Type type) {
    return GSON.fromJson(json, type);
  }

  public Set<State> getStates() {
    return states;
  }

  public Set<String> getAlphabet() {
    return alphabet;
  }

  public Set<State> getInitialStates() {
    return initialStates;
  }

  public Set<State> getAcceptingStates() {
    return acceptingStates;
  }

  public TransitionFunction<ActionType> getTransitionFunction() {
    return transitionFunction;
  }
}
