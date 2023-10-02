package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.TupleUtil.stringTuple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;
import me.paultristanwagner.modelchecking.ts.CyclePath;

public class NBA {

  private static final Gson GSON;

  static {
    GSON =
        new GsonBuilder()
            .registerTypeAdapter(NBATransition.class, new NBATransition.NBATransitionAdapter())
            .setPrettyPrinting()
            .create();
  }

  private final Set<String> states;
  private final Set<String> alphabet;
  private final Set<String> initialStates;
  private final Set<String> acceptingStates;
  private final Set<NBATransition> transitions;

  public NBA(
      Set<String> states,
      Set<String> alphabet,
      Set<String> initialStates,
      Set<String> acceptingStates,
      Set<NBATransition> transitions) {
    this.states = states;
    this.alphabet = alphabet;
    this.initialStates = initialStates;
    this.acceptingStates = acceptingStates;
    this.transitions = transitions;
  }

  public Set<String> getSuccessors(String state) {
    Set<String> successors = new HashSet<>();
    for (NBATransition transition : transitions) {
      if (transition.getFrom().equals(state)) {
        successors.add(transition.getTo());
      }
    }
    return successors;
  }

  public Set<String> getSuccessors(String state, String action) {
    Set<String> successors = new HashSet<>();
    for (NBATransition transition : transitions) {
      if (!transition.getFrom().equals(state)) {
        continue;
      }

      // todo: make this more efficient
      String a = transition.getAction();
      String b = action;
      Set<String> left = new HashSet<>(Arrays.asList(a.substring(1, a.length() - 1).split(", ")));
      Set<String> right = new HashSet<>(Arrays.asList(b.substring(1, b.length() - 1).split(", ")));

      boolean actionMatches = left.equals(right);

      if (actionMatches) {
        successors.add(transition.getTo());
      }
    }
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

  public GNBA toGNBA() {
    Set<String> states = new HashSet<>(this.states);
    Set<String> alphabet = new HashSet<>(this.alphabet);
    Set<String> initialStates = new HashSet<>(this.initialStates);
    Set<NBATransition> transitions = new HashSet<>(this.transitions);

    Set<Set<String>> acceptingSets = new HashSet<>();
    acceptingSets.add(new HashSet<>(this.acceptingStates));

    return new GNBA(states, alphabet, initialStates, acceptingSets, transitions);
  }

  public GNBA product(NBA other) {
    GNBABuilder builder = new GNBABuilder();
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

    for (NBATransition transition : transitions) {
      for (NBATransition otherTransition : other.transitions) {
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

  public static NBA fromJson(String json) {
    return GSON.fromJson(json, NBA.class);
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

  public Set<NBATransition> getTransitions() {
    return transitions;
  }
}
