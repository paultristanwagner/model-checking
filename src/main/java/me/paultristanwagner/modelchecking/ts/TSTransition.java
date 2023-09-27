package me.paultristanwagner.modelchecking.ts;

import com.google.gson.*;

public class TSTransition {

  private final String from;
  private final String to;

  private TSTransition(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public static TSTransition of(String from, String to) {
    return new TSTransition(from, to);
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public static class TSTransitionAdapter
      implements JsonSerializer<TSTransition>, JsonDeserializer<TSTransition> {

    @Override
    public JsonElement serialize(
        TSTransition transition,
        java.lang.reflect.Type typeOfSrc,
        JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(transition.getFrom());
      jsonArray.add(transition.getTo());

      return jsonArray;
    }

    @Override
    public TSTransition deserialize(
        JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray tuple = json.getAsJsonArray();
      String from = tuple.get(0).getAsString();
      String to = tuple.get(1).getAsString();
      return TSTransition.of(from, to);
    }
  }
}
