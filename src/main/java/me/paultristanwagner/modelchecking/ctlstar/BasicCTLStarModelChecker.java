package me.paultristanwagner.modelchecking.ctlstar;

import static me.paultristanwagner.modelchecking.ltl.formula.LTLNotFormula.not;

import java.util.*;
import me.paultristanwagner.modelchecking.automaton.State;
import me.paultristanwagner.modelchecking.ctlstar.formula.*;
import me.paultristanwagner.modelchecking.ltl.BasicLTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ts.BasicTransitionSystem;

public class BasicCTLStarModelChecker implements CTLStarModelChecker {

  @Override
  public CTLStarModelCheckingResult check(BasicTransitionSystem ts, CTLStarFormula formula) {
    ts = (BasicTransitionSystem) ts.copy();
    while (true) {
      Set<CTLStarFormula> stateSubFormulas = getNonTrivialMinimalStateSubFormulas(formula);

      List<CTLStarFormula> stateSubFormulasList = new ArrayList<>(stateSubFormulas);
      stateSubFormulasList.sort(Comparator.comparingInt(CTLStarFormula::getDepth));

      if (stateSubFormulasList.isEmpty()) {
        stateSubFormulasList.add(formula);
      }

      for (CTLStarFormula subFormula : stateSubFormulasList) {
        if (subFormula != formula && subFormula instanceof CTLStarIdentifierFormula) continue;

        Set<State> satStates = sat(ts, subFormula);

        if (subFormula == formula) {
          if (satStates.containsAll(ts.getInitialStates())) {
            return CTLStarModelCheckingResult.models();
          } else {
            return CTLStarModelCheckingResult.doesNotModel();
          }
        }

        String freshAtomicProposition = ts.introduceFreshAtomicProposition();
        for (State satState : satStates) {
          ts.addLabel(satState, freshAtomicProposition);
        }
        formula.replaceFormula(subFormula, freshAtomicProposition);
      }
    }
  }

  @Override
  public Set<State> sat(BasicTransitionSystem ts, CTLStarFormula formula) {
    if (formula instanceof CTLStarTrueFormula) {
      return satTrue(ts);
    } else if (formula instanceof CTLStarFalseFormula) {
      return satFalse(ts);
    } else if (formula instanceof CTLStarIdentifierFormula identifierFormula) {
      return satIdentifier(ts, identifierFormula);
    } else if (formula instanceof CTLStarAndFormula andFormula) {
      return satAnd(ts, andFormula);
    } else if (formula instanceof CTLStarOrFormula orFormula) {
      return satOr(ts, orFormula);
    } else if (formula instanceof CTLStarNotFormula notFormula) {
      return satNot(ts, notFormula);
    } else if (formula instanceof CTLStarParenthesisFormula parenthesisFormula) {
      return sat(ts, parenthesisFormula.getFormula());
    } else if (formula instanceof CTLStarExistsFormula existsFormula) {
      return satExists(ts, existsFormula);
    } else if (formula instanceof CTLStarAllFormula allFormula) {
      return satAll(ts, allFormula);
    }

    throw new UnsupportedOperationException();
  }

  private Set<State> satExists(BasicTransitionSystem ts, CTLStarExistsFormula existsFormula) {
    LTLModelChecker ltlModelChecker = new BasicLTLModelChecker();
    LTLFormula ltlFormula = existsFormula.getFormula().toLTL();
    LTLFormula negated = not(ltlFormula);
    Set<State> negation = ltlModelChecker.sat(ts, negated);
    Set<State> sat = new HashSet<>(ts.getStates());
    sat.removeAll(negation);
    return sat;
  }

  private Set<State> satAll(BasicTransitionSystem ts, CTLStarAllFormula allFormula) {
    LTLModelChecker ltlModelChecker = new BasicLTLModelChecker();
    LTLFormula ltlFormula = allFormula.getFormula().toLTL();
    return ltlModelChecker.sat(ts, ltlFormula);
  }

  private Set<State> satTrue(BasicTransitionSystem ts) {
    return new HashSet<>(ts.getStates());
  }

  private Set<State> satFalse(BasicTransitionSystem ts) {
    return new HashSet<>();
  }

  private Set<State> satIdentifier(BasicTransitionSystem ts, CTLStarIdentifierFormula formula) {
    String atomicProposition = formula.getIdentifier();

    Set<State> satStates = new HashSet<>();
    for (State state : ts.getStates()) {
      if (ts.getLabel(state).contains(atomicProposition)) {
        satStates.add(state);
      }
    }

    return satStates;
  }

  private Set<State> satAnd(BasicTransitionSystem ts, CTLStarAndFormula andFormula) {
    Set<State> satStates = new HashSet<>(ts.getStates());
    for (CTLStarFormula component : andFormula.getComponents()) {
      satStates.retainAll(sat(ts, component));
    }
    return satStates;
  }

  private Set<State> satOr(BasicTransitionSystem ts, CTLStarOrFormula orFormula) {
    Set<State> satStates = new HashSet<>();
    for (CTLStarFormula component : orFormula.getComponents()) {
      satStates.addAll(sat(ts, component));
    }
    return satStates;
  }

  private Set<State> satNot(BasicTransitionSystem ts, CTLStarNotFormula notFormula) {
    Set<State> satStates = new HashSet<>(ts.getStates());
    satStates.removeAll(sat(ts, notFormula.getFormula()));
    return satStates;
  }

  private Set<CTLStarFormula> getStateSubFormulas(CTLStarFormula formula) {
    Set<CTLStarFormula> subFormulas = formula.getSubFormulas();
    List<CTLStarFormula> subFormulasList = new ArrayList<>(subFormulas);
    subFormulasList.sort(Comparator.comparingInt(CTLStarFormula::getDepth));

    Set<CTLStarFormula> stateSubFormulas = new HashSet<>();
    for (CTLStarFormula subFormula : subFormulasList) {
      if (subFormula instanceof CTLStarNextFormula
          || subFormula instanceof CTLStarUntilFormula
          || subFormula instanceof CTLStarEventuallyFormula
          || subFormula instanceof CTLStarAlwaysFormula) {
        continue;
      }

      if (subFormula instanceof CTLStarTrueFormula
          || subFormula instanceof CTLStarFalseFormula
          || subFormula instanceof CTLStarIdentifierFormula
          || subFormula instanceof CTLStarExistsFormula
          || subFormula instanceof CTLStarAllFormula) {
        stateSubFormulas.add(subFormula);
        continue;
      }

      if (subFormula instanceof CTLStarAndFormula andFormula) { // depends
        boolean allStateSubFormulas = true;
        for (CTLStarFormula component : andFormula.getComponents()) {
          if (!stateSubFormulas.contains(component)) {
            allStateSubFormulas = false;
            break;
          }
        }

        if (allStateSubFormulas) {
          stateSubFormulas.add(andFormula);
        }
      } else if (subFormula instanceof CTLStarOrFormula orFormula) { // depends
        boolean allStateSubFormulas = true;
        for (CTLStarFormula component : orFormula.getComponents()) {
          if (!stateSubFormulas.contains(component)) {
            allStateSubFormulas = false;
            break;
          }
        }

        if (allStateSubFormulas) {
          stateSubFormulas.add(orFormula);
        }
      } else if (subFormula instanceof CTLStarNotFormula notFormula) { // depends
        if (stateSubFormulas.contains(notFormula.getFormula())) {
          stateSubFormulas.add(notFormula);
        }
      } else if (subFormula instanceof CTLStarParenthesisFormula parenthesisFormula) { // depends
        if (stateSubFormulas.contains(parenthesisFormula.getFormula())) {
          stateSubFormulas.add(parenthesisFormula);
        }
      } else {
        throw new UnsupportedOperationException();
      }
    }

    return stateSubFormulas;
  }

  private Set<CTLStarFormula> getNonTrivialMinimalStateSubFormulas(CTLStarFormula formula) {
    Set<CTLStarFormula> result = getStateSubFormulas(formula);
    result.removeIf(subFormula -> subFormula instanceof CTLStarIdentifierFormula);
    result.removeIf(subFormula -> subFormula instanceof CTLStarTrueFormula);

    boolean changed = true;
    while (changed) {
      changed = false;

      for (CTLStarFormula subFormula : result) {
        Set<CTLStarFormula> subSubFormulas = subFormula.getProperSubFormulas();
        for (CTLStarFormula subSubFormula : subSubFormulas) {
          if (result.contains(subSubFormula)) {
            result.remove(subFormula);
            changed = true;
          }
        }

        if (changed) {
          break;
        }
      }
    }

    return result;
  }
}
