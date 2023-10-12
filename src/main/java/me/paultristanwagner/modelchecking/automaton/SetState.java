package me.paultristanwagner.modelchecking.automaton;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetState<T> extends State {

  protected Set<T> set;

  public SetState(Set<T> set) {
    this.set = set;
  }

  public Set<T> getSet() {
    return set;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SetState<?> setState = (SetState<?>) o;
    return Objects.equals(set, setState.set);
  }

  @Override
  public int hashCode() {
    return Objects.hash(set);
  }

  public static class SetStateJsonAdapter<T>
      implements JsonSerializer<SetState<T>>, JsonDeserializer<SetState<T>> {

    @Override
    public SetState<T> deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonArray jsonArray = json.getAsJsonArray();
      Set<T> set = new HashSet<>();
      for (JsonElement jsonElement : jsonArray) {
        set.add(context.deserialize(jsonElement, new TypeToken<T>() {}.getType()));
      }
      return new SetState<>(set);
    }

    @Override
    public JsonElement serialize(
        SetState<T> src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray jsonArray = new JsonArray();
      for (T t : src.set) {
        jsonArray.add(context.serialize(t, new TypeToken<T>() {}.getType()));
      }
      return jsonArray;
    }
  }
}
