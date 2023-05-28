package me.paultristanwagner.modelchecking;

import me.paultristanwagner.modelchecking.ctl.BasicCTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.CTLModelChecker;
import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ctl.parse.CTLParser;
import me.paultristanwagner.modelchecking.parse.SyntaxError;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static me.paultristanwagner.modelchecking.util.AnsiColor.*;

public class Main {
  public static void main(String[] args) throws IOException {
    PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out), true, UTF_8);

    Scanner scanner = new Scanner(System.in);

    TransitionSystem ts = null;
    while(ts == null) {
      File file = null;
      while(file == null) {
        ps.print("Enter file for Transition System: ");
        String input = scanner.nextLine();
        file = new File(input);
        if(!file.exists()) {
          ps.println("File does not exist!");
          System.out.println();
          file = null;
        } else if(file.isDirectory()) {
          ps.println("File is a directory!");
          System.out.println();
          file = null;
        }
      }
      
      String fileContent = new String( Files.readAllBytes(file.toPath()) );
      try {
        ts = TransitionSystem.fromJson(fileContent);
        ps.println(GREEN + "Transition System loaded!" + RESET);
      } catch (RuntimeException exception) {
        ps.println(RED + "Errrr: " + exception.getMessage() + RESET);
      }
    }

    while (true) {
      ps.print("Enter CTL Formula: ");
      String input = scanner.nextLine();

      if (input.equals("exit")) {
        break;
      }

      long before = System.currentTimeMillis();
      CTLParser parser = new CTLParser();
      try {
        CTLFormula formula = parser.parse(input);
        ps.println("ϕ := " + formula);

        CTLModelChecker modelChecker = new BasicCTLModelChecker();
        boolean result = modelChecker.check(ts, formula);

        if (result) {
          ps.println(GREEN + "TS ⊨ ϕ" + RESET);
        } else {
          ps.println(RED + "TS ⊭ ϕ" + RESET);
        }
      } catch (SyntaxError error) {
        ps.print(RED);
        error.printWithContext();
        ps.print(RESET);
      }
      long after = System.currentTimeMillis();

      long time_ms = after - before;
      ps.println(GRAY + "(" + time_ms + " ms)" + RESET);
      ps.println();
    }
  }
}
