package me.paultristanwagner.modelchecking.ctl;

import static me.paultristanwagner.modelchecking.ctl.CTLModelCheckingResult.doesNotModel;
import static me.paultristanwagner.modelchecking.ctl.CTLModelCheckingResult.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLAlwaysFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLNextFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLUntilFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public class BasicCTLModelChecker implements CTLModelChecker {

  @Override
  public CTLModelCheckingResult check(TransitionSystem ts, CTLFormula formula) {
    CTLENFConverter converter = new CTLENFConverter();
    CTLFormula enfFormula = converter.convert(formula);

    Set<String> satStates = sat(ts, enfFormula);

    boolean models = satStates.containsAll(ts.getInitialStates());

    if (models) {
      return models();
    } else {
      return doesNotModel();
    }
  }

  private Set<String> sat(TransitionSystem ts, CTLFormula formula) {
    if (formula instanceof CTLIdentifierFormula identifierFormula) {
      return satIdentifier(ts, identifierFormula);
    } else if (formula instanceof CTLNotFormula notFormula) {
      return satNot(ts, notFormula);
    } else if (formula instanceof CTLOrFormula orFormula) {
      return satOr(ts, orFormula);
    } else if (formula instanceof CTLAndFormula andFormula) {
      return satAnd(ts, andFormula);
    } else if (formula instanceof CTLTrueFormula trueFormula) {
      return satTrue(ts, trueFormula);
    } else if (formula instanceof CTLFalseFormula falseFormula) {
      return satFalse(ts, falseFormula);
    } else if (formula instanceof CTLExistsFormula existsFormula) {
      return satExists(ts, existsFormula);
    } else if (formula instanceof CTLParenthesisFormula parenthesisFormula) {
      return sat(ts, parenthesisFormula.getInner());
    }

    throw new UnsupportedOperationException();
  }

  private Set<String> satExists(TransitionSystem ts, CTLExistsFormula formula) {
    CTLPathFormula pathFormula = formula.getPathFormula();
    if (pathFormula instanceof CTLNextFormula nextFormula) {
      return satExistsNext(ts, nextFormula);
    } else if (pathFormula instanceof CTLUntilFormula untilFormula) {
      return satExistsUntil(ts, untilFormula);
    } else if (pathFormula instanceof CTLAlwaysFormula alwaysFormula) {
      return satExistsAlways(ts, alwaysFormula);
    }

    throw new UnsupportedOperationException();
  }

  private Set<String> satExistsNext(TransitionSystem ts, CTLNextFormula nextFormula) {
    Set<String> result = new HashSet<>();
    Set<String> satStates = sat(ts, nextFormula.getStateFormula());

    for (String state : ts.getStates()) {
      List<String> successors = ts.getSuccessors(state);

      boolean hasSatSuccessor = false;
      for (String successor : successors) {
        if (satStates.contains(successor)) {
          hasSatSuccessor = true;
          break;
        }
      }

      if (hasSatSuccessor) {
        result.add(state);
      }
    }

    return result;
  }

  private Set<String> satExistsUntil(TransitionSystem ts, CTLUntilFormula untilFormula) {
    CTLFormula left = untilFormula.getLeft();
    CTLFormula right = untilFormula.getRight();

    Set<String> satLeft = sat(ts, left);

    Set<String> T = sat(ts, right);

    boolean changed = true;
    while (changed) {
      changed = false;

      for (String state : satLeft) {
        if (T.contains(state)) {
          continue;
        }

        List<String> successors = ts.getSuccessors(state);
        boolean hasTSuccessor = false;
        for (String successor : successors) {
          if (T.contains(successor)) {
            hasTSuccessor = true;
            break;
          }
        }

        if (hasTSuccessor) {
          T.add(state);
          changed = true;
        }
      }
    }

    return T;
  }

  private Set<String> satExistsAlways(TransitionSystem ts, CTLAlwaysFormula formula) {
    CTLFormula stateFormula = formula.getStateFormula();

    Set<String> V = sat(ts, stateFormula);

    boolean changed = true;
    while (changed) {
      changed = false;

      Iterator<String> iterator = V.iterator();
      while (iterator.hasNext()) {
        String state = iterator.next();
        List<String> successors = ts.getSuccessors(state);
        boolean hasVSuccessor = false;
        for (String successor : successors) {
          if (V.contains(successor)) {
            hasVSuccessor = true;
            break;
          }
        }

        if (!hasVSuccessor) {
          iterator.remove();
          changed = true;
        }
      }
    }

    return V;
  }

  private Set<String> satIdentifier(TransitionSystem ts, CTLIdentifierFormula formula) {
    String atomicProposition = formula.getIdentifier();

    Set<String> satStates = new HashSet<>();
    for (String state : ts.getStates()) {
      if (ts.getLabel(state).contains(atomicProposition)) {
        satStates.add(state);
      }
    }

    return satStates;
  }

  private Set<String> satNot(TransitionSystem ts, CTLNotFormula formula) {
    Set<String> satStates = sat(ts, formula.getArgument());
    Set<String> allStates = new HashSet<>(ts.getStates());

    Set<String> result = new HashSet<>(allStates);
    result.removeAll(satStates);

    return result;
  }

  private Set<String> satOr(TransitionSystem ts, CTLOrFormula formula) {
    Set<String> result = new HashSet<>();
    List<CTLFormula> components = formula.getComponents();
    for (CTLFormula component : components) {
      result.addAll(sat(ts, component));
    }

    return result;
  }

  private Set<String> satAnd(TransitionSystem ts, CTLAndFormula formula) {
    Set<String> result = new HashSet<>(ts.getStates());
    List<CTLFormula> components = formula.getComponents();
    for (CTLFormula component : components) {
      result.retainAll(sat(ts, component));
    }

    return result;
  }

  private Set<String> satTrue(TransitionSystem ts, CTLTrueFormula formula) {
    return new HashSet<>(ts.getStates());
  }

  private Set<String> satFalse(TransitionSystem ts, CTLFalseFormula formula) {
    return new HashSet<>();
  }
}
