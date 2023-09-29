package me.paultristanwagner.modelchecking;

import static me.paultristanwagner.modelchecking.Main.OUT;
import static me.paultristanwagner.modelchecking.util.Symbol.CHECKMARK;
import static me.paultristanwagner.modelchecking.util.Symbol.CROSS;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import me.paultristanwagner.modelchecking.ctlstar.BasicCTLStarModelChecker;
import me.paultristanwagner.modelchecking.ctlstar.CTLStarModelChecker;
import me.paultristanwagner.modelchecking.ctlstar.CTLStarModelCheckingResult;
import me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarFormula;
import me.paultristanwagner.modelchecking.ctlstar.parse.CTLStarParser;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;
import me.paultristanwagner.modelchecking.ts.TransitionSystemLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CTLStarModelCheckerTest {

  private static final String TS_PATH = "examples/ts0.json";

  private static final String[] CTL_STAR_ASSUMED =
      new String[] {
        "not a",
        "exists eventually all always b",
        "exists next exists next (a)",
        "all eventually b",
        "exists next not b",
        "all always all eventually b"
      };

  private static final String[] CTL_STAR_NOT_ASSUMED =
      new String[] {
        "not a and not b",
        "all always exists eventually (not a and not b)",
        "exists always exists next (not a and not b)"
      };

  private static TransitionSystem ts;
  private static CTLStarParser parser;
  private static CTLStarModelChecker modelChecker;

  @BeforeAll
  public static void setUp() throws IOException {
    ts = TransitionSystemLoader.load(TS_PATH);
    parser = new CTLStarParser();
    modelChecker = new BasicCTLStarModelChecker();
  }

  @Test
  public void testAssumed() {
    OUT.println("Checking assumed CTL* formulas ... ");

    boolean errors = false;

    for (String ctlStar : CTL_STAR_ASSUMED) {
      CTLStarFormula formula = parser.parse(ctlStar);
      OUT.print("Checking " + formula + " ...");
      CTLStarModelCheckingResult result = modelChecker.check(ts, formula);

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
    OUT.println("Checking not assumed CTL* formulas ... ");

    boolean errors = false;

    for (String ctlStar : CTL_STAR_NOT_ASSUMED) {
      CTLStarFormula formula = parser.parse(ctlStar);
      OUT.print("Checking " + formula + " ...");
      CTLStarModelCheckingResult result = modelChecker.check(ts, formula);
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
