package me.paultristanwagner.modelchecking;

import static java.nio.charset.StandardCharsets.UTF_8;
import static me.paultristanwagner.modelchecking.util.AnsiColor.*;
import static me.paultristanwagner.modelchecking.util.Symbol.MODELS_SYMBOL;
import static me.paultristanwagner.modelchecking.util.Symbol.NOT_MODELS_SYMBOL;

import java.io.*;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import me.paultristanwagner.modelchecking.ctl.BasicCTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.CTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.parse.CTLParser;
import me.paultristanwagner.modelchecking.ctlstar.BasicCTLStarModelChecker;
import me.paultristanwagner.modelchecking.ctlstar.formula.CTLStarFormula;
import me.paultristanwagner.modelchecking.ctlstar.parse.CTLStarParser;
import me.paultristanwagner.modelchecking.ltl.BasicLTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelCheckingResult;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.parse.LTLParser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;
import me.paultristanwagner.modelchecking.ts.CyclePath;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;
import me.paultristanwagner.modelchecking.util.Symbol;

public class Main {

  public static final PrintStream OUT =
      new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8);
  public static final Scanner SCANNER = new Scanner(System.in);

  public static void main(String[] args) {
    TransitionSystem ts = enterTransitionSystem();

    while (true) {
      OUT.print("Enter formula: ");
      String input;
      try {
        input = SCANNER.nextLine();
      } catch (NoSuchElementException e) {
        break;
      }

      if (input.equals("exit")) {
        break;
      }

      long before = System.currentTimeMillis();
      Formula formula = parseFormula(input);
      if (formula == null) {
        continue;
      }

      String phiSymbol;

      ModelCheckingResult result;
      Optional<CyclePath> counterExample = Optional.empty();
      if (formula instanceof LTLFormula ltlFormula) {
        phiSymbol = Symbol.LOWERCASE_PHI_SYMBOL;
        OUT.println(phiSymbol + " := " + formula + GRAY + " (LTL)" + RESET);

        LTLModelChecker modelChecker = new BasicLTLModelChecker();
        LTLModelCheckingResult ltlModelCheckingResult = modelChecker.check(ts, ltlFormula);
        result = ltlModelCheckingResult;

        if (!ltlModelCheckingResult.isModel()) {
          counterExample = Optional.of(ltlModelCheckingResult.getCounterExample());
        }
      } else if (formula instanceof CTLFormula ctlFormula) {
        phiSymbol = Symbol.UPPERCASE_PHI_SYMBOL;

        OUT.println(phiSymbol + " := " + formula + GRAY + " (CTL)" + RESET);

        CTLModelChecker modelChecker = new BasicCTLModelChecker();
        result = modelChecker.check(ts, ctlFormula);
      } else if (formula instanceof CTLStarFormula ctlStarFormula) {
        phiSymbol = Symbol.UPPERCASE_PHI_SYMBOL;

        OUT.println(phiSymbol + " := " + formula + GRAY + " (CTL*)" + RESET);

        BasicCTLStarModelChecker modelChecker = new BasicCTLStarModelChecker();
        result = modelChecker.check(ts, ctlStarFormula);
      } else {
        OUT.println(RED + "Unknown formula type" + RESET);
        continue;
      }

      if (result.isModel()) {
        OUT.println(GREEN + "TS " + MODELS_SYMBOL + " " + phiSymbol + RESET);
      } else {
        OUT.println(RED + "TS " + NOT_MODELS_SYMBOL + " " + phiSymbol);
        counterExample.ifPresent(infinitePath -> OUT.println("Counterexample: " + infinitePath));
        OUT.print(RESET);
      }

      long after = System.currentTimeMillis();

      long time_ms = after - before;
      OUT.println(GRAY + "(" + time_ms + " ms)" + RESET);
      OUT.println();
    }
  }

  private static Formula parseFormula(String input) {
    boolean ltl = input.toLowerCase().startsWith("ltl ");
    boolean ctl = input.toLowerCase().startsWith("ctl ");
    boolean ctlStar = input.toLowerCase().startsWith("ctl* ");

    if (ltl || ctl) {
      input = input.substring(4);
    } else if (ctlStar) {
      input = input.substring(5);
    }

    if (ltl) {
      try {
        return parseLTLFormula(input);
      } catch (SyntaxError error) {
        OUT.print(RED);
        error.printWithContext();
        OUT.print(RESET);
      }

      return null;
    }

    if (ctl) {
      try {
        return parseCTLFormula(input);
      } catch (SyntaxError error) {
        OUT.print(RED);
        error.printWithContext();
        OUT.print(RESET);
      }

      return null;
    }

    if (ctlStar) {
      try {
        return parseCTLStarFormula(input);
      } catch (SyntaxError error) {
        OUT.print(RED);
        error.printWithContext();
        OUT.print(RESET);
      }

      return null;
    }

    return parseAnyFormula(input);
  }

  private static Formula parseAnyFormula(String input) {
    SyntaxError ctlError;
    SyntaxError ltlError;
    SyntaxError ctlStarError;
    try {
      return parseCTLFormula(input);
    } catch (SyntaxError e) {
      ctlError = e;
    }

    try {
      return parseLTLFormula(input);
    } catch (SyntaxError e) {
      ltlError = e;
    }

    try {
      return parseCTLStarFormula(input);
    } catch (SyntaxError e) {
      ctlStarError = e;
    }

    OUT.print(RED);
    OUT.println("Could not parse either CTL, LTL or CTL* formula!");
    OUT.print("CTL: ");
    ctlError.printWithContext();
    OUT.print("LTL: ");
    ltlError.printWithContext();
    OUT.print("CTL*: ");
    ctlStarError.printWithContext();
    OUT.print(RESET);

    return null;
  }

  private static LTLFormula parseLTLFormula(String string) {
    LTLParser ltlParser = new LTLParser();
    return ltlParser.parse(string);
  }

  private static CTLFormula parseCTLFormula(String string) {
    CTLParser ctlParser = new CTLParser();
    return ctlParser.parse(string);
  }

  private static CTLStarFormula parseCTLStarFormula(String string) {
    CTLStarParser ctlStarParser = new CTLStarParser();
    return ctlStarParser.parse(string);
  }

  private static TransitionSystem enterTransitionSystem() {
    TransitionSystem ts = null;
    while (ts == null) {
      File file = null;
      while (file == null) {
        OUT.print("Enter file for Transition System: ");
        String input;
        try {
          input = SCANNER.nextLine();
        } catch (NoSuchElementException e) {
          return null;
        }

        file = new File(input);
        if (!file.exists()) {
          OUT.println(RED + "Error: File does not exist!" + RESET);
          OUT.println();
          file = null;
        } else if (file.isDirectory()) {
          OUT.println(RED + "Error: File is a directory!" + RESET);
          OUT.println();
          file = null;
        }
      }

      String fileContent;
      try {
        fileContent = new String(Files.readAllBytes(file.toPath()));
      } catch (IOException e) {
        OUT.println(RED + "Error: Could not read file" + RESET);
        continue;
      }

      try {
        ts = TransitionSystem.fromJson(fileContent);
        OUT.println(GREEN + "Transition System loaded!" + RESET);
      } catch (SyntaxError error) {
        OUT.print(RED);
        error.printWithContext();
        OUT.print(RESET);
      }
    }

    return ts;
  }
}
