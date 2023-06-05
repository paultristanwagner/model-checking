package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GNBA {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(NBATransition.class, new NBATransition.NBATransitionAdapter())
                .setPrettyPrinting()
                .create();
    }

    private final List<String> states;
    private final List<String> alphabet;
    private final List<String> initialStates;
    private final List<List<String>> acceptingSets;
    private final List<NBATransition> transitions;

    public GNBA(List<String> states, List<String> alphabet, List<String> initialStates, List<List<String>> acceptingSets, List<NBATransition> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialStates = initialStates;
        this.acceptingSets = acceptingSets;
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

    public NBA convertToNBA() {
        NBABuilder builder = new NBABuilder();
        builder.setAlphabet(alphabet);

        for (String state : states) {
            for (int i = 0; i < acceptingSets.size(); i++) {
                String nbaState = "(" + state + "," + (i + 1) + ")";
                builder.addState(nbaState);
            }
        }

        for (String initialState : initialStates) {
            String nbaInitialState = "(" + initialState + ",1)";
            builder.addInitialState(nbaInitialState);
        }

        if(!acceptingSets.isEmpty()) {
            List<String> acceptingSet = acceptingSets.get(0);
            for (String acceptingState : acceptingSet) {
                String nbaAcceptingState = "(" + acceptingState + ",1)";
                builder.addAcceptingState(nbaAcceptingState);
            }
        }

        for (NBATransition transition : transitions) {
            for (int i = 0; i < acceptingSets.size(); i++) {
                List<String> acceptingSet = acceptingSets.get(i);
                String nbaFrom = "(" + transition.getFrom() + "," + (i + 1) + ")";
                String nbaTo;
                if (acceptingSet.contains(transition.getFrom())) {
                    nbaTo = "(" + transition.getTo() + "," + ((i + 1) % acceptingSets.size() + 1) + ")";
                } else {
                    nbaTo = "(" + transition.getTo() + "," + (i + 1) + ")";
                }
                builder.addTransition(nbaFrom, transition.getAction(), nbaTo);
            }
        }

        return builder.build();
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static GNBA fromJson(String json) {
        return GSON.fromJson(json, GNBA.class);
    }

    public List<String> getStates() {
        return states;
    }

    public List<String> getInitialStates() {
        return initialStates;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public List<List<String>> getAcceptingSets() {
        return acceptingSets;
    }

    public List<NBATransition> getTransitions() {
        return transitions;
    }
}
