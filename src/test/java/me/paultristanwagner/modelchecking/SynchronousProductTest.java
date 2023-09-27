package me.paultristanwagner.modelchecking;

import java.io.IOException;
import java.io.InputStream;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SynchronousProductTest {

  @Test
  public void testSynchronousProduct() throws IOException {
    InputStream tsInputStream =
        getClass().getClassLoader().getResourceAsStream("ts_message_delivery.json");
    String tsJson = new String(tsInputStream.readAllBytes());
    TransitionSystem ts = TransitionSystem.fromJson(tsJson);

    InputStream nbaInputStream =
        getClass().getClassLoader().getResourceAsStream("nba_never_delivered.json");
    String nbaJson = new String(nbaInputStream.readAllBytes());
    NBA nba = NBA.fromJson(nbaJson);

    TransitionSystem result = ts.reachableSynchronousProduct(nba);
    Assertions.assertNotNull(result);

    Assertions.assertEquals(10, result.getStates().size());
    Assertions.assertEquals(1, result.getInitialStates().size());
  }
}
