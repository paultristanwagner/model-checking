package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.*;

public class NBATransition {

    private final String from;
    private final String action;
    private final String to;

    private NBATransition(String from, String action, String to) {
        this.from = from;
        this.action = action;
        this.to = to;
    }

    public static NBATransition of(String from, String action, String to) {
        return new NBATransition(from, action, to);
    }

    public String getFrom() {
        return from;
    }

    public String getAction() {
        return action;
    }

    public String getTo() {
        return to;
    }

    public static class NBATransitionAdapter
            implements JsonSerializer<NBATransition>, JsonDeserializer<NBATransition> {

        @Override
        public JsonElement serialize(
                NBATransition transition, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(transition.getFrom());
            jsonArray.add(transition.getAction());
            jsonArray.add(transition.getTo());

            return jsonArray;
        }

        @Override
        public NBATransition deserialize(
                JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray tuple = json.getAsJsonArray();
            String from = tuple.get(0).getAsString();
            String action = tuple.get(1).getAsString();
            String to = tuple.get(2).getAsString();
            return NBATransition.of(from, action, to);
        }
    }
}
