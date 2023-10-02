package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;
import static me.paultristanwagner.modelchecking.util.Pair.pair;
import static me.paultristanwagner.modelchecking.util.TupleUtil.stringTuple;

import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.annotations.SerializedName;
import me.paultristanwagner.modelchecking.ts.CyclePath;
import me.paultristanwagner.modelchecking.util.Pair;

public class NBA<ActionType> {

  private final Set<String> states;
  private final Set<String> alphabet;
  private final Set<String> initialStates;
  private final Set<String> acceptingStates;
  private final Set<NBATransition<ActionType>> transitions;

  private final transient Map<String, Set<String>> successorCache;
  private final transient Map<Pair<String, ActionType>, Set<String>> successorActionCache;

  private NBA() {
    this.states = new HashSet<>();
    this.alphabet = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.acceptingStates = new HashSet<>();
    this.transitions = new HashSet<>();

    this.successorCache = new HashMap<>();
    this.successorActionCache = new HashMap<>();
  }

  public NBA(
      Set<String> states,
      Set<String> alphabet,
      Set<String> initialStates,
      Set<String> acceptingStates,
      Set<NBATransition<ActionType>> transitions) {
    this.states = states;
    this.alphabet = alphabet;
    this.initialStates = initialStates;
    this.acceptingStates = acceptingStates;
    this.transitions = transitions;

    this.successorCache = new HashMap<>();
    this.successorActionCache = new HashMap<>();
  }

  public Set<String> getSuccessors(String state) {
    if(successorCache.containsKey(state)) {
      return successorCache.get(state);
    }

    Set<String> successors = new HashSet<>();
    for (NBATransition<ActionType> transition : transitions) {
      if (transition.getFrom().equals(state)) {
        successors.add(transition.getTo());
      }
    }

    successorCache.put(state, successors);

    return successors;
  }

  public Set<String> getSuccessors(String state, ActionType action) {
    if(successorActionCache.containsKey(pair(state, action))) {
      return successorActionCache.get(pair(state, action));
    }

    Set<String> successors = new HashSet<>();
    for (NBATransition<ActionType> transition : transitions) {
      if (!transition.getFrom().equals(state)) {
        continue;
      }

      if (transition.getAction().equals(action)) {
        successors.add(transition.getTo());
      }
    }

    successorActionCache.put(pair(state, action), successors);

    return successors;
  }

  private boolean cycleCheck(String s, Set<String> v, Stack<String> xi) {
    xi.push(s);
    v.add(s);
    while (!xi.isEmpty()) {
      String s1 = xi.peek();
      Set<String> successors = getSuccessors(s1);
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

  public NBAEmptinessResult checkEmptiness() {
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
          if (acceptingStates.contains(s) && cycleCheck(s, v, xi)) {
            List<String> piList = new ArrayList<>(pi);
            List<String> xiList = new ArrayList<>(xi);

            CyclePath witness = new CyclePath(piList, xiList);
            return NBAEmptinessResult.nonEmpty(witness);
          }
        }
      }
    }

    return NBAEmptinessResult.empty();
  }

  public GNBA<ActionType> toGNBA() {
    Set<String> states = new HashSet<>(this.states);
    Set<String> alphabet = new HashSet<>(this.alphabet);
    Set<String> initialStates = new HashSet<>(this.initialStates);
    Set<NBATransition<ActionType>> transitions = new HashSet<>(this.transitions);

    Set<Set<String>> acceptingSets = new HashSet<>();
    acceptingSets.add(new HashSet<>(this.acceptingStates));

    return new GNBA<>(states, alphabet, initialStates, acceptingSets, transitions);
  }

  public GNBA<ActionType> product(NBA<ActionType> other) {
    GNBABuilder<ActionType> builder = new GNBABuilder<>();
    builder.setAlphabet(alphabet);

    for (String state1 : states) {
      for (String state2 : other.states) {
        String state = stringTuple(state1, state2);
        builder.addState(state);
      }
    }

    for (String initialState : initialStates) {
      for (String otherInitialState : other.initialStates) {
        String state = stringTuple(initialState, otherInitialState);
        builder.addInitialState(state);
      }
    }

    for (NBATransition<ActionType> transition : transitions) {
      for (NBATransition<ActionType> otherTransition : other.transitions) {
        if (!transition.getAction().equals(otherTransition.getAction())) {
          continue;
        }

        String from = stringTuple(transition.getFrom(), otherTransition.getFrom());
        String to = stringTuple(transition.getTo(), otherTransition.getTo());
        builder.addTransition(from, transition.getAction(), to);
      }
    }

    Set<String> acceptingSet1 = new HashSet<>();
    for (String acceptingState : acceptingStates) {
      for (String state : other.states) {
        String newAcceptingState = stringTuple(acceptingState, state);
        acceptingSet1.add(newAcceptingState);
      }
    }

    Set<String> acceptingSet2 = new HashSet<>();
    for (String acceptingState : other.acceptingStates) {
      for (String state : states) {
        String newAcceptingState = stringTuple(state, acceptingState);
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

  public Set<String> getStates() {
    return states;
  }

  public Set<String> getAlphabet() {
    return alphabet;
  }

  public Set<String> getInitialStates() {
    return initialStates;
  }

  public Set<String> getAcceptingStates() {
    return acceptingStates;
  }

  public Set<NBATransition<ActionType>> getTransitions() {
    return transitions;
  }
}
