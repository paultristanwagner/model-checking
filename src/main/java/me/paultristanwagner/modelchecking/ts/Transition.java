package me.paultristanwagner.modelchecking.ts;

import com.google.gson.*;

public class Transition {

  private final String from;
  private final String to;

  private Transition(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public static Transition of(String from, String to) {
    return new Transition(from, to);
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public static class TransitionAdapter
      implements JsonSerializer<Transition>, JsonDeserializer<Transition> {

    @Override
    public JsonElement serialize(
        Transition transition, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(transition.getFrom());
      jsonArray.add(transition.getTo());

      return jsonArray;
    }

    @Override
    public Transition deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      String from = tuple.get(0).getAsString();
      String to = tuple.get(1).getAsString();
      return Transition.of(from, to);
    }
  }
}
