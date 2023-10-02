package me.paultristanwagner.modelchecking.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.NBATransition;
import me.paultristanwagner.modelchecking.ts.TSTransition;

public class GsonUtil {

  static {
    GSON =
        new GsonBuilder()
            .registerTypeAdapter(TSTransition.class, new TSTransition.TSTransitionAdapter())
            .registerTypeAdapter(new TypeToken<NBATransition<String>>() {}.getType(), new NBATransition.BasicNBATransitionAdapter())
            .registerTypeAdapter(new TypeToken<NBATransition<Set<String>>>() {}.getType(), new NBATransition.AdvancedNBATransitionAdapter())
            .setPrettyPrinting()
            .create();
  }

  public static final Gson GSON;
}
