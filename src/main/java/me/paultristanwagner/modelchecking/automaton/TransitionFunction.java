package me.paultristanwagner.modelchecking.automaton;

import static me.paultristanwagner.modelchecking.util.Pair.pair;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.paultristanwagner.modelchecking.util.Pair;

public class TransitionFunction<ActionType> {

  private final Set<NBATransition<ActionType>> transitions;
  private final Set<ActionType> actions;
  private final Map<State, Set<State>> stateSuccessors;
  private final Map<Pair<State, ActionType>, Set<State>> stateActionSuccessors;
  private final Map<ActionType, Set<NBATransition<ActionType>>> actionTransitionMap;

  public TransitionFunction() {
    this.transitions = new HashSet<>();
    this.actions = new HashSet<>();
    this.stateSuccessors = new HashMap<>();
    this.stateActionSuccessors = new HashMap<>();
    this.actionTransitionMap = new HashMap<>();
  }

  public TransitionFunction(TransitionFunction<ActionType> other) {
    this.transitions = new HashSet<>(other.transitions);
    this.actions = new HashSet<>();
    this.stateSuccessors = new HashMap<>(other.stateSuccessors);
    this.stateActionSuccessors = new HashMap<>(other.stateActionSuccessors);
    this.actionTransitionMap = new HashMap<>(other.actionTransitionMap);
  }

  public void addTransition(State from, ActionType action, State to) {
    NBATransition<ActionType> transition = new NBATransition<>(from, action, to);
    transitions.add(transition);
    actions.add(action);

    Set<State> successors = stateSuccessors.getOrDefault(from, new HashSet<>());
    successors.add(to);
    stateSuccessors.put(from, successors);

    Set<State> actionSuccessors =
        stateActionSuccessors.getOrDefault(pair(from, action), new HashSet<>());
    actionSuccessors.add(to);
    stateActionSuccessors.put(pair(from, action), actionSuccessors);

    Set<NBATransition<ActionType>> actionTransitions =
        actionTransitionMap.getOrDefault(action, new HashSet<>());
    actionTransitions.add(transition);
    actionTransitionMap.put(action, actionTransitions);
  }

  public Set<ActionType> getActions() {
    return actions;
  }

  public Set<State> getSuccessors(State state, ActionType action) {
    return stateActionSuccessors.getOrDefault(pair(state, action), new HashSet<>());
  }

  public Set<State> getSuccessors(State state) {
    return stateSuccessors.getOrDefault(state, new HashSet<>());
  }

  public Set<NBATransition<ActionType>> getTransitions(ActionType action) {
    return actionTransitionMap.getOrDefault(action, new HashSet<>());
  }

  public Set<NBATransition<ActionType>> getTransitions() {
    return transitions;
  }

  public static class BasicTransitionFunctionAdapter
      implements JsonSerializer<TransitionFunction<String>>,
          JsonDeserializer<TransitionFunction<String>> {

    @Override
    public JsonElement serialize(
        TransitionFunction<String> transitionFunction,
        java.lang.reflect.Type typeOfSrc,
        JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      for (NBATransition<String> transition : transitionFunction.getTransitions()) {
        jsonArray.add(context.serialize(transition));
      }

      return jsonArray;
    }

    @Override
    public TransitionFunction<String> deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      TransitionFunction<String> transitionFunction = new TransitionFunction<>();
      JsonArray jsonArray = json.getAsJsonArray();
      for (JsonElement jsonElement : jsonArray) {
        NBATransition<String> transition =
            context.deserialize(jsonElement, new TypeToken<NBATransition<String>>() {}.getType());
        transitionFunction.addTransition(
            transition.getFrom(), transition.getAction(), transition.getTo());
      }

      return transitionFunction;
    }
  }

  public static class AdvancedTransitionFunctionAdapter
      implements JsonSerializer<TransitionFunction<Set<String>>>,
          JsonDeserializer<TransitionFunction<Set<String>>> {

    @Override
    public JsonElement serialize(
        TransitionFunction<Set<String>> transitionFunction,
        java.lang.reflect.Type typeOfSrc,
        JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      for (NBATransition<Set<String>> transition : transitionFunction.getTransitions()) {
        jsonArray.add(context.serialize(transition));
      }

      return jsonArray;
    }

    @Override
    public TransitionFunction<Set<String>> deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      TransitionFunction<Set<String>> transitionFunction = new TransitionFunction<>();
      JsonArray jsonArray = json.getAsJsonArray();
      for (JsonElement jsonElement : jsonArray) {
        NBATransition<Set<String>> transition =
            context.deserialize(
                jsonElement, new TypeToken<NBATransition<Set<String>>>() {}.getType());
        transitionFunction.addTransition(
            transition.getFrom(), transition.getAction(), transition.getTo());
      }

      return transitionFunction;
    }
  }
}
