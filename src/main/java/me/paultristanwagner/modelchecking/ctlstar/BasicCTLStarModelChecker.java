package me.paultristanwagner.modelchecking.ctlstar;

import static me.paultristanwagner.modelchecking.ltl.formula.LTLNotFormula.not;

import java.util.*;
import me.paultristanwagner.modelchecking.ctlstar.formula.*;
import me.paultristanwagner.modelchecking.ltl.BasicLTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.LTLModelChecker;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public class BasicCTLStarModelChecker implements CTLStarModelChecker {

  @Override
  public CTLStarModelCheckingResult check(TransitionSystem ts, CTLStarFormula formula) {
    ts = ts.copy();
    while (true) {
      Set<CTLStarFormula> stateSubFormulas = getNonTrivialMinimalStateSubFormulas(formula);

      List<CTLStarFormula> stateSubFormulasList = new ArrayList<>(stateSubFormulas);
      stateSubFormulasList.sort(Comparator.comparingInt(CTLStarFormula::getDepth));

      if (stateSubFormulasList.isEmpty()) {
        stateSubFormulasList.add(formula);
      }

      for (CTLStarFormula subFormula : stateSubFormulasList) {
        if (subFormula != formula && subFormula instanceof CTLStarIdentifierFormula) continue;

        Set<String> satStates = sat(ts, subFormula);

        if (subFormula == formula) {
          if (satStates.containsAll(ts.getInitialStates())) {
            return CTLStarModelCheckingResult.models();
          } else {
            return CTLStarModelCheckingResult.doesNotModel();
          }
        }

        String freshAtomicProposition = ts.introduceFreshAtomicProposition();
        for (String satState : satStates) {
          ts.addLabel(satState, freshAtomicProposition);
        }
        formula.replaceFormula(subFormula, freshAtomicProposition);
      }
    }
  }

  @Override
  public Set<String> sat(TransitionSystem ts, CTLStarFormula formula) {
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

  private Set<String> satExists(TransitionSystem ts, CTLStarExistsFormula existsFormula) {
    LTLModelChecker ltlModelChecker = new BasicLTLModelChecker();
    LTLFormula ltlFormula = existsFormula.getFormula().toLTL();
    LTLFormula negated = not(ltlFormula);
    Set<String> negation = ltlModelChecker.sat(ts, negated);
    Set<String> sat = new HashSet<>(ts.getStates());
    sat.removeAll(negation);
    return sat;
  }

  private Set<String> satAll(TransitionSystem ts, CTLStarAllFormula allFormula) {
    LTLModelChecker ltlModelChecker = new BasicLTLModelChecker();
    LTLFormula ltlFormula = allFormula.getFormula().toLTL();
    return ltlModelChecker.sat(ts, ltlFormula);
  }

  private Set<String> satTrue(TransitionSystem ts) {
    return new HashSet<>(ts.getStates());
  }

  private Set<String> satFalse(TransitionSystem ts) {
    return new HashSet<>();
  }

  private Set<String> satIdentifier(TransitionSystem ts, CTLStarIdentifierFormula formula) {
    String atomicProposition = formula.getIdentifier();

    Set<String> satStates = new HashSet<>();
    for (String state : ts.getStates()) {
      if (ts.getLabel(state).contains(atomicProposition)) {
        satStates.add(state);
      }
    }

    return satStates;
  }

  private Set<String> satAnd(TransitionSystem ts, CTLStarAndFormula andFormula) {
    Set<String> satStates = new HashSet<>(ts.getStates());
    for (CTLStarFormula component : andFormula.getComponents()) {
      satStates.retainAll(sat(ts, component));
    }
    return satStates;
  }

  private Set<String> satOr(TransitionSystem ts, CTLStarOrFormula orFormula) {
    Set<String> satStates = new HashSet<>();
    for (CTLStarFormula component : orFormula.getComponents()) {
      satStates.addAll(sat(ts, component));
    }
    return satStates;
  }

  private Set<String> satNot(TransitionSystem ts, CTLStarNotFormula notFormula) {
    Set<String> satStates = new HashSet<>(ts.getStates());
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
