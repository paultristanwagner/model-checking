package me.paultristanwagner.modelchecking;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import me.paultristanwagner.modelchecking.automaton.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GNBATest {

  @Test
  public void testNBAConstruction() throws IOException {
    InputStream gnbaIs =
        getClass().getClassLoader().getResourceAsStream("gnba_inf_crit1_and_inf_crit2.json");
    String gnbaJson = new String(gnbaIs.readAllBytes());
    GNBA<String> gnba = GNBA.fromJson(gnbaJson, new TypeToken<GNBA<String>>() {}.getType());
    NBA<String> nba = gnba.convertToNBA();

    NBAEmptinessResult nbaEmptinessResult = nba.checkEmptiness();
    Assertions.assertFalse(nbaEmptinessResult.isEmpty());
    Assertions.assertEquals(6, nba.getStates().size());
    Assertions.assertEquals(1, nba.getInitialStates().size());
    Assertions.assertEquals(1, nba.getAcceptingStates().size());
  }

  @Test
  public void testNonEmptyProduct() throws IOException {
    InputStream nba1Is = getClass().getClassLoader().getResourceAsStream("nba_inf_a_1.json");
    InputStream nba2Is = getClass().getClassLoader().getResourceAsStream("nba_inf_a_2.json");

    String nba1Json = new String(nba1Is.readAllBytes());
    String nba2Json = new String(nba2Is.readAllBytes());

    NBA<String> nba1 = NBA.fromJson(nba1Json, new TypeToken<NBA<String>>() {}.getType());
    NBA<String> nba2 = NBA.fromJson(nba2Json, new TypeToken<NBA<String>>() {}.getType());

    GNBA<String> product = nba1.product(nba2);
    NBA<String> nbaResult = product.convertToNBA();

    NBAEmptinessResult nbaEmptinessResult = nbaResult.checkEmptiness();
    Assertions.assertFalse(nbaEmptinessResult.isEmpty());
  }

  @Test
  public void testEmptyProduct() throws IOException {
    InputStream nba1Is = getClass().getClassLoader().getResourceAsStream("nba_fin_a.json");
    InputStream nba2Is = getClass().getClassLoader().getResourceAsStream("nba_inf_a_1.json");

    String nba1Json = new String(nba1Is.readAllBytes());
    String nba2Json = new String(nba2Is.readAllBytes());

    NBA<String> nba1 = NBA.fromJson(nba1Json, new TypeToken<NBA<String>>() {}.getType());
    NBA<String> nba2 = NBA.fromJson(nba2Json, new TypeToken<NBA<String>>() {}.getType());

    GNBA<String> product = nba1.product(nba2);
    NBA<String> nbaResult = product.convertToNBA();

    NBAEmptinessResult nbaEmptinessResult = nbaResult.checkEmptiness();
    Assertions.assertTrue(nbaEmptinessResult.isEmpty());
  }
}
