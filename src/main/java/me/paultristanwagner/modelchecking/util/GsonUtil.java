package me.paultristanwagner.modelchecking.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.*;

public class GsonUtil {

  static {
    GSON =
        new GsonBuilder()
            .registerTypeAdapter(
                new TypeToken<TransitionFunction<String>>() {}.getType(),
                new TransitionFunction.BasicTransitionFunctionAdapter())
            .registerTypeAdapter(
                new TypeToken<TransitionFunction<Set<String>>>() {}.getType(),
                new TransitionFunction.AdvancedTransitionFunctionAdapter())
            .registerTypeAdapter(
                new TypeToken<NBATransition<Set<String>>>() {}.getType(),
                new NBATransition.AdvancedNBATransitionAdapter())
            .registerTypeAdapter(
                new TypeToken<NBATransition<String>>() {}.getType(),
                new NBATransition.BasicNBATransitionAdapter())
            .registerTypeAdapter(State.class, new BasicState.BasicStateJsonAdapter())
            .registerTypeAdapter(BasicState.class, new BasicState.BasicStateJsonAdapter())
            .registerTypeAdapter(
                new TypeToken<SetState<String>>() {}.getType(),
                new SetState.SetStateJsonAdapter<String>())
            .setPrettyPrinting()
            .create();
  }

  public static final Gson GSON;
}
