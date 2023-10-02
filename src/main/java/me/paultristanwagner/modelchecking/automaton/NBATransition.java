package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Set;

public class NBATransition<ActionType> {

  private final String from;
  private final ActionType action;
  private final String to;

  protected NBATransition(String from, ActionType action, String to) {
    this.from = from;
    this.action = action;
    this.to = to;
  }

  public static <ActionType> NBATransition<ActionType> of(
      String from, ActionType action, String to) {
    return new NBATransition<>(from, action, to);
  }

  public String getFrom() {
    return from;
  }

  public ActionType getAction() {
    return action;
  }

  public String getTo() {
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
      jsonArray.add(transition.getFrom());
      jsonArray.add(
          context.serialize(transition.getAction(), new TypeToken<String>() {}.getType()));
      jsonArray.add(transition.getTo());

      return jsonArray;
    }

    @Override
    public NBATransition<String> deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      String from = tuple.get(0).getAsString();
      String action = tuple.get(1).getAsString();
      String to = tuple.get(2).getAsString();
      return NBATransition.of(from, action, to);
    }
  }

  public static class AdvancedNBATransitionAdapter
      implements JsonSerializer<NBATransition<Set<String>>>,
          JsonDeserializer<NBATransition<Set<String>>> {

    @Override
    public JsonElement serialize(
        NBATransition<Set<String>> transition,
        java.lang.reflect.Type typeOfSrc,
        JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(transition.getFrom());
      jsonArray.add(
          context.serialize(transition.getAction(), new TypeToken<Set<String>>() {}.getType()));
      jsonArray.add(transition.getTo());

      return jsonArray;
    }

    @Override
    public NBATransition<Set<String>> deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      String from = tuple.get(0).getAsString();
      Set<String> action =
          context.deserialize(tuple.get(1), new TypeToken<Set<String>>() {}.getType());
      String to = tuple.get(2).getAsString();
      return NBATransition.of(from, action, to);
    }
  }
}
