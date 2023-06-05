package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class NBA {

    private static final Gson GSON;

    static {
        GSON =
                new GsonBuilder()
                        .registerTypeAdapter(NBATransition.class, new NBATransition.NBATransitionAdapter())
                        .setPrettyPrinting()
                        .create();
    }

    private final List<String> states;
    private final List<String> alphabet;
    private final List<String> initialStates;
    private final List<String> acceptingStates;
    private final List<NBATransition> transitions;

    public NBA(List<String> states, List<String> alphabet, List<String> initialStates, List<String> acceptingStates, List<NBATransition> transitions) {
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
            if (transition.getFrom().equals(state) && transition.getAction().equals(action)) {
                successors.add(transition.getTo());
            }
        }
        return successors;
    }

    private boolean cycleCheck(String s, Set<String> v, Stack<String> xi) {
        xi.push(s);
        v.add(s);
        while(!xi.isEmpty()) {
            String s1 = xi.peek();
            Set<String> successors = getSuccessors(s1);
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

    public NBAEmptinessResult checkEmptiness() {
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
                    if(acceptingStates.contains(s) && cycleCheck(s, v, xi)) {
                        List<String> piList = new ArrayList<>(pi);
                        List<String> xiList = new ArrayList<>(xi);

                        NBAEmptinessWitness witness = new NBAEmptinessWitness(piList, xiList);
                        return NBAEmptinessResult.nonEmpty(witness);
                    }
                }
            }
        }

        return NBAEmptinessResult.empty();
    }

    public GNBA toGNBA() {
        List<String> states = new ArrayList<>(this.states);
        List<String> alphabet = new ArrayList<>(this.alphabet);
        List<String> initialStates = new ArrayList<>(this.initialStates);
        List<NBATransition> transitions = new ArrayList<>(this.transitions);

        List<List<String>> acceptingSets = new ArrayList<>();
        acceptingSets.add(new ArrayList<>(this.acceptingStates));

        return new GNBA(states, alphabet, initialStates, acceptingSets, transitions);
    }

    public GNBA product(NBA other) {
        GNBABuilder builder = new GNBABuilder();
        builder.setAlphabet(alphabet);

        for (String state1 : states) {
            for (String state2 : other.states) {
                String state = "(" + state1 + "," + state2 + ")";
                builder.addState(state);
            }
        }

        for (String initialState : initialStates) {
            for (String otherInitialState : other.initialStates) {
                String state = "(" + initialState + "," + otherInitialState + ")";
                builder.addInitialState(state);
            }
        }

        for (NBATransition transition : transitions) {
            for (NBATransition otherTransition : other.transitions) {
                if(!transition.getAction().equals(otherTransition.getAction())) {
                    continue;
                }

                String from = "(" + transition.getFrom() + "," + otherTransition.getFrom() + ")";
                String to = "(" + transition.getTo() + "," + otherTransition.getTo() + ")";
                builder.addTransition(from, transition.getAction(), to);
            }
        }

        Set<String> acceptingSet1 = new HashSet<>();
        for (String acceptingState : acceptingStates) {
            for (String state : other.states) {
                String newAcceptingState = "(" + acceptingState + "," + state + ")";
                acceptingSet1.add(newAcceptingState);
            }
        }

        Set<String> acceptingSet2 = new HashSet<>();
        for (String acceptingState : other.acceptingStates) {
            for (String state : states) {
                String newAcceptingState = "(" + state + "," + acceptingState + ")";
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

    public List<String> getStates() {
        return states;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public List<String> getInitialStates() {
        return initialStates;
    }

    public List<String> getAcceptingStates() {
        return acceptingStates;
    }

    public List<NBATransition> getTransitions() {
        return transitions;
    }
}
