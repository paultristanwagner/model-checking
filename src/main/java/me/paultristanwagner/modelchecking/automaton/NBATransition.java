package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Set;

public class NBATransition<ActionType> {

  private final State from;
  private final ActionType action;
  private final State to;

  protected NBATransition(State from, ActionType action, State to) {
    this.from = from;
    this.action = action;
    this.to = to;
  }

  public static <ActionType> NBATransition<ActionType> of(State from, ActionType action, State to) {
    return new NBATransition<>(from, action, to);
  }

  public State getFrom() {
    return from;
  }

  public ActionType getAction() {
    return action;
  }

  public State getTo() {
    return to;
  }

  public static class BasicNBATransitionAdapter
      implements JsonSerializer<NBATransition<String>>, JsonDeserializer<NBATransition<String>> {

    @Override
    public JsonElement serialize(
        NBATransition<String> transition,
        java.lang.reflect.Type typeOfSrc,
        JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(context.serialize(transition.getFrom()));
      jsonArray.add(
          context.serialize(transition.getAction(), new TypeToken<String>() {}.getType()));
      jsonArray.add(context.serialize(transition.getTo()));

      return jsonArray;
    }

    @Override
    public NBATransition<String> deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      State from = context.deserialize(tuple.get(0), BasicState.class);
      String action = tuple.get(1).getAsString();
      State to = context.deserialize(tuple.get(2), BasicState.class);
      return NBATransition.of(from, action, to);
    }
  }

  public static class AdvancedNBATransitionAdapter
      implements JsonSerializer<NBATransition<Set<String>>>,
          JsonDeserializer<NBATransition<Set<String>>> {

    @Override
    public JsonElement serialize(
        NBATransition<Set<String>> src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(context.serialize(src.getFrom()));
      jsonArray.add(context.serialize(src.getAction(), new TypeToken<Set<String>>() {}.getType()));
      jsonArray.add(context.serialize(src.getTo()));

      return jsonArray;
    }

    @Override
    public NBATransition<Set<String>> deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      State from = context.deserialize(tuple.get(0), BasicState.class);
      Set<String> action =
          context.deserialize(tuple.get(1), new TypeToken<Set<String>>() {}.getType());
      State to = context.deserialize(tuple.get(2), BasicState.class);
      return NBATransition.of(from, action, to);
    }
  }
}
