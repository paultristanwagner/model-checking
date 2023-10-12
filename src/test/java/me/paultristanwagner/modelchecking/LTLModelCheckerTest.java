package me.paultristanwagner.modelchecking;

import static me.paultristanwagner.modelchecking.Main.OUT;
import static me.paultristanwagner.modelchecking.util.Symbol.CHECKMARK;
import static me.paultristanwagner.modelchecking.util.Symbol.CROSS;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import me.paultristanwagner.modelchecking.ltl.BasicLTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelCheckingResult;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.parse.LTLParser;
import me.paultristanwagner.modelchecking.ts.BasicTransitionSystem;
import me.paultristanwagner.modelchecking.ts.TransitionSystemLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LTLModelCheckerTest {

  private static final String TS_PATH = "examples/ts0.json";

  private static final String[] LTL_ASSUMED =
      new String[] {
        "(not a and not b) or (b)",
        "always eventually b",
        "(always eventually a) or (eventually always b)"
      };

  private static final String[] LTL_NOT_ASSUMED =
      new String[] {
        "a and b",
        "b",
        "a",
        "always eventually not b",
        "(always eventually a) and (eventually always b)"
      };

  private static BasicTransitionSystem ts;
  private static LTLParser parser;
  private static LTLModelChecker modelChecker;

  @BeforeAll
  public static void setUp() throws IOException {
    ts = TransitionSystemLoader.load(TS_PATH);
    parser = new LTLParser();
    modelChecker = new BasicLTLModelChecker();
  }

  @Test
  public void testAssumed() {
    OUT.println("Checking assumed LTL formulas ... ");

    boolean errors = false;

    for (String ltl : LTL_ASSUMED) {
      LTLFormula formula = parser.parse(ltl);
      OUT.print("Checking " + formula + " ...");
      LTLModelCheckingResult result = modelChecker.check(ts, formula);

      if (result.isModel()) {
        OUT.println(" " + CHECKMARK);
      } else {
        errors = true;
        OUT.println(" " + CROSS);
      }
    }

    OUT.println();

    assertFalse(errors);
  }

  @Test
  public void testNotAssumed() {
    OUT.println("Checking not assumed LTL formulas ... ");

    boolean errors = false;

    for (String ltl : LTL_NOT_ASSUMED) {
      LTLFormula formula = parser.parse(ltl);
      OUT.print("Checking " + formula + " ...");
      LTLModelCheckingResult result = modelChecker.check(ts, formula);
      if (result.isModel()) {
        errors = true;
        OUT.println(" " + CROSS);
      } else {
        OUT.println(" " + CHECKMARK);
      }
    }

    OUT.println();

    assertFalse(errors);
  }
}
