package me.paultristanwagner.modelchecking.ctl;

import static me.paultristanwagner.modelchecking.ctl.CTLModelCheckingResult.doesNotModel;
import static me.paultristanwagner.modelchecking.ctl.CTLModelCheckingResult.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.State;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLAlwaysFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLNextFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLPathFormula;
import me.paultristanwagner.modelchecking.ctl.formula.path.CTLUntilFormula;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;
import me.paultristanwagner.modelchecking.ts.BasicTransitionSystem;

public class BasicCTLModelChecker implements CTLModelChecker {

  @Override
  public CTLModelCheckingResult check(BasicTransitionSystem ts, CTLFormula formula) {
    CTLENFConverter converter = new CTLENFConverter();
    CTLFormula enfFormula = converter.convert(formula);

    Set<State> satStates = sat(ts, enfFormula);

    boolean models = satStates.containsAll(ts.getInitialStates());

    if (models) {
      return models();
    } else {
      return doesNotModel();
    }
  }

  @Override
  public Set<State> sat(BasicTransitionSystem ts, CTLFormula formula) {
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

  private Set<State> satExists(BasicTransitionSystem ts, CTLExistsFormula formula) {
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

  private Set<State> satExistsNext(BasicTransitionSystem ts, CTLNextFormula nextFormula) {
    Set<State> result = new HashSet<>();
    Set<State> satStates = sat(ts, nextFormula.getStateFormula());

    for (State state : ts.getStates()) {
      Set<State> successors = ts.getSuccessors(state);

      boolean hasSatSuccessor = false;
      for (State successor : successors) {
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

  private Set<State> satExistsUntil(BasicTransitionSystem ts, CTLUntilFormula untilFormula) {
    CTLFormula left = untilFormula.getLeft();
    CTLFormula right = untilFormula.getRight();

    Set<State> satLeft = sat(ts, left);

    Set<State> T = sat(ts, right);

    boolean changed = true;
    while (changed) {
      changed = false;

      for (State state : satLeft) {
        if (T.contains(state)) {
          continue;
        }

        Set<State> successors = ts.getSuccessors(state);
        boolean hasTSuccessor = false;
        for (State successor : successors) {
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

  private Set<State> satExistsAlways(BasicTransitionSystem ts, CTLAlwaysFormula formula) {
    CTLFormula stateFormula = formula.getStateFormula();

    Set<State> V = sat(ts, stateFormula);

    boolean changed = true;
    while (changed) {
      changed = false;

      Iterator<State> iterator = V.iterator();
      while (iterator.hasNext()) {
        State state = iterator.next();
        Set<State> successors = ts.getSuccessors(state);
        boolean hasVSuccessor = false;
        for (State successor : successors) {
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

  private Set<State> satIdentifier(BasicTransitionSystem ts, CTLIdentifierFormula formula) {
    String atomicProposition = formula.getIdentifier();

    Set<State> satStates = new HashSet<>();
    for (State state : ts.getStates()) {
      if (ts.getLabel(state).contains(atomicProposition)) {
        satStates.add(state);
      }
    }

    return satStates;
  }

  private Set<State> satNot(BasicTransitionSystem ts, CTLNotFormula formula) {
    Set<State> satStates = sat(ts, formula.getArgument());
    Set<State> allStates = new HashSet<>(ts.getStates());

    Set<State> result = new HashSet<>(allStates);
    result.removeAll(satStates);

    return result;
  }

  private Set<State> satOr(BasicTransitionSystem ts, CTLOrFormula formula) {
    Set<State> result = new HashSet<>();
    List<CTLFormula> components = formula.getComponents();
    for (CTLFormula component : components) {
      result.addAll(sat(ts, component));
    }

    return result;
  }

  private Set<State> satAnd(BasicTransitionSystem ts, CTLAndFormula formula) {
    Set<State> result = new HashSet<>(ts.getStates());
    List<CTLFormula> components = formula.getComponents();
    for (CTLFormula component : components) {
      result.retainAll(sat(ts, component));
    }

    return result;
  }

  private Set<State> satTrue(BasicTransitionSystem ts, CTLTrueFormula formula) {
    return new HashSet<>(ts.getStates());
  }

  private Set<State> satFalse(BasicTransitionSystem ts, CTLFalseFormula formula) {
    return new HashSet<>();
  }
}
