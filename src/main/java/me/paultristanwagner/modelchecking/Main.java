package me.paultristanwagner.modelchecking;

import me.paultristanwagner.modelchecking.ctl.BasicCTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.CTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.parse.CTLParser;
import me.paultristanwagner.modelchecking.ltl.BasicLTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelCheckingResult;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ltl.parse.LTLParser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;
import me.paultristanwagner.modelchecking.ts.InfinitePath;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;
import me.paultristanwagner.modelchecking.util.Symbol;

import java.io.*;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static me.paultristanwagner.modelchecking.util.AnsiColor.*;
import static me.paultristanwagner.modelchecking.util.Symbol.MODELS_SYMBOL;
import static me.paultristanwagner.modelchecking.util.Symbol.NOT_MODELS_SYMBOL;

public class Main {

    public static final PrintStream OUT = new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8);
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
            Optional<InfinitePath> counterExample = Optional.empty();
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

        if (ltl || ctl) {
            input = input.substring(4);
        }

        if (ltl) {
            try {
                return parseLTLFormula(input);
            } catch (SyntaxError error) {
                OUT.print(RED);
                error.printWithContext();
                OUT.print(RESET);
            }
        } else if (ctl) {
            try {
                return parseCTLFormula(input);
            } catch (SyntaxError error) {
                OUT.print(RED);
                error.printWithContext();
                OUT.print(RESET);
            }
        } else {
            try {
                return parseCTLFormula(input);
            } catch (SyntaxError error1) {
                try {
                    return parseLTLFormula(input);
                } catch (SyntaxError error2) {
                    OUT.print(RED);
                    OUT.println("Could not parse either CTL or LTL formula!");
                    OUT.print("CTL: ");
                    error1.printWithContext();
                    OUT.print("LTL: ");
                    error2.printWithContext();
                    OUT.print(RESET);
                }
            }
        }

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
