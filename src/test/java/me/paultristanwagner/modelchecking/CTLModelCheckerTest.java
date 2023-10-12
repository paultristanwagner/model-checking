package me.paultristanwagner.modelchecking;

import static me.paultristanwagner.modelchecking.Main.OUT;
import static me.paultristanwagner.modelchecking.util.Symbol.CHECKMARK;
import static me.paultristanwagner.modelchecking.util.Symbol.CROSS;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import me.paultristanwagner.modelchecking.ctl.BasicCTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.CTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.CTLModelCheckingResult;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.parse.CTLParser;
import me.paultristanwagner.modelchecking.ts.BasicTransitionSystem;
import me.paultristanwagner.modelchecking.ts.TransitionSystemLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CTLModelCheckerTest {

  private static final String TS_PATH = "examples/ts0.json";

  private static final String[] CTL_ASSUMED =
      new String[] {
        "not a",
        "exists eventually all always b",
        "exists next exists next (a)",
        "all eventually b",
        "exists next not b",
        "all always all eventually b"
      };

  private static final String[] CTL_NOT_ASSUMED =
      new String[] {
        "not a and not b",
        "all always exists eventually (not a and not b)",
        "exists always exists next (not a and not b)"
      };

  private static BasicTransitionSystem ts;
  private static CTLParser parser;
  private static CTLModelChecker modelChecker;

  @BeforeAll
  public static void setUp() throws IOException {
    ts = TransitionSystemLoader.load(TS_PATH);
    parser = new CTLParser();
    modelChecker = new BasicCTLModelChecker();
  }

  @Test
  public void testAssumed() {
    OUT.println("Checking assumed CTL formulas ... ");

    boolean errors = false;

    for (String ctl : CTL_ASSUMED) {
      CTLFormula formula = parser.parse(ctl);
      OUT.print("Checking " + formula + " ...");
      CTLModelCheckingResult result = modelChecker.check(ts, formula);

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
    OUT.println("Checking not assumed CTL formulas ... ");

    boolean errors = false;

    for (String ctl : CTL_NOT_ASSUMED) {
      CTLFormula formula = parser.parse(ctl);
      OUT.print("Checking " + formula + " ...");
      CTLModelCheckingResult result = modelChecker.check(ts, formula);
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
