package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Objects;

public class BasicState extends State {

  protected final String name;

  public BasicState(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BasicState that = (BasicState) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public static class BasicStateJsonAdapter
      implements JsonSerializer<BasicState>, JsonDeserializer<BasicState> {

    @Override
    public BasicState deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return new BasicState(json.getAsString());
    }

    @Override
    public JsonElement serialize(BasicState src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.name);
    }
  }
}
