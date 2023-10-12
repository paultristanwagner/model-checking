package me.paultristanwagner.modelchecking;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.automaton.State;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SynchronousProductTest {

  @Test
  public void testSynchronousProduct() throws IOException {
    InputStream tsInputStream =
        getClass().getClassLoader().getResourceAsStream("ts_message_delivery.json");
    String tsJson = new String(tsInputStream.readAllBytes());
    TransitionSystem<String> ts =
        GSON.fromJson(tsJson, new TypeToken<TransitionSystem<String>>() {}.getType());

    InputStream nbaInputStream =
        getClass().getClassLoader().getResourceAsStream("nba_never_delivered.json");
    String nbaJson = new String(nbaInputStream.readAllBytes());
    NBA<Set<String>> nba = NBA.fromJson(nbaJson, new TypeToken<NBA<Set<String>>>() {}.getType());

    TransitionSystem<State> result = ts.reachableSynchronousProduct(nba);
    Assertions.assertNotNull(result);

    Assertions.assertEquals(10, result.getStates().size());
    Assertions.assertEquals(1, result.getInitialStates().size());
  }
}
