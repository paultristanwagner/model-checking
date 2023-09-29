package me.paultristanwagner.modelchecking.ltl;

import static me.paultristanwagner.modelchecking.ltl.LTLModelCheckingResult.doesNotModel;
import static me.paultristanwagner.modelchecking.ltl.LTLModelCheckingResult.models;
import static me.paultristanwagner.modelchecking.ltl.formula.LTLIdentifierFormula.identifier;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import me.paultristanwagner.modelchecking.automaton.GNBA;
import me.paultristanwagner.modelchecking.automaton.GNBABuilder;
import me.paultristanwagner.modelchecking.automaton.NBA;
import me.paultristanwagner.modelchecking.ltl.formula.*;
import me.paultristanwagner.modelchecking.ts.CyclePath;
import me.paultristanwagner.modelchecking.ts.TSPersistenceResult;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public class BasicLTLModelChecker implements LTLModelChecker {

  @Override
  public LTLModelCheckingResult check(TransitionSystem ts, LTLFormula formula) {
    ts = ts.copy();

    LTLFormula negation = formula.negate();

    GNBA gnba = computeGNBA(ts, negation);

    NBA nba = gnba.convertToNBA();
    TransitionSystem synchronousProduct = ts.reachableSynchronousProduct(nba);

    Set<String> persistentStates = new HashSet<>(nba.getStates());
    nba.getAcceptingStates().forEach(persistentStates::remove);

    TSPersistenceResult result = synchronousProduct.checkPersistence(persistentStates);
    if (result.isPersistent()) {
      return models();
    } else {
      CyclePath counterExample = result.getWitness().reduce();
      return doesNotModel(counterExample);
    }
  }

  @Override
  public Set<String> sat(TransitionSystem ts, LTLFormula formula) {
    Set<String> result = new HashSet<>();

    LTLFormula negation = formula.negate();
    GNBA gnba = computeGNBA(ts, negation);
    NBA nba = gnba.convertToNBA();

    for (String state : ts.getStates()) {
      TransitionSystem initial = ts.copy();
      initial.clearInitialStates();
      initial.addInitialState(state);

      TransitionSystem synchronousProduct = initial.reachableSynchronousProduct(nba);

      Set<String> persistentStates = new HashSet<>(nba.getStates());
      nba.getAcceptingStates().forEach(persistentStates::remove);

      TSPersistenceResult persistenceResult = synchronousProduct.checkPersistence(persistentStates);
      if (persistenceResult.isPersistent()) {
        result.add(state);
      }
    }

    return result;
  }

  private GNBA computeGNBA(TransitionSystem ts, LTLFormula formula) {
    Set<String> atomicPropositions = new HashSet<>(ts.getAtomicPropositions());
    Set<LTLFormula> closure = formula.getClosure();
    Set<Guess> elementarySets = computeElementarySets(atomicPropositions, closure);

    GNBABuilder gnbaBuilder = new GNBABuilder();

    /*
     * Compute the states and transitions of the GNBA
     *
     * We added the following additional transition rules:
     * (1) always phi in B <=> phi in B and always phi in B'
     * (2) eventually phi in B <=> phi in B or eventually phi in B'
     */
    for (Guess one : elementarySets) {
      gnbaBuilder.addState(one.toString());

      Set<String> assumedAtomicPropositions = one.assumedAtomicPropositions();

      for (Guess potentialSuccessor : elementarySets) {
        boolean violates = false;
        for (LTLFormula ltlFormula : closure) {
          if (ltlFormula instanceof LTLNextFormula nextFormula) {
            if (one.isAssumed(nextFormula)
                != potentialSuccessor.isAssumed(nextFormula.getFormula())) {
              violates = true;
              break;
            }
          } else if (ltlFormula instanceof LTLUntilFormula untilFormula) {
            boolean lhs = one.isAssumed(untilFormula);
            boolean rhs =
                one.isAssumed(untilFormula.getRight())
                    || (one.isAssumed(untilFormula.getLeft())
                        && potentialSuccessor.isAssumed(untilFormula));

            if (lhs != rhs) {
              violates = true;
              break;
            }
          } else if (ltlFormula instanceof LTLEventuallyFormula eventuallyFormula) {
            boolean lhs = one.isAssumed(eventuallyFormula);
            boolean rhs =
                one.isAssumed(eventuallyFormula.getFormula())
                    || potentialSuccessor.isAssumed(eventuallyFormula);

            if (lhs != rhs) {
              violates = true;
              break;
            }
          } else if (ltlFormula instanceof LTLAlwaysFormula alwaysFormula) {
            boolean lhs = one.isAssumed(alwaysFormula);
            boolean rhs =
                one.isAssumed(alwaysFormula.getFormula())
                    && potentialSuccessor.isAssumed(alwaysFormula);

            if (lhs != rhs) {
              violates = true;
              break;
            }
          }
        }

        if (violates) {
          continue;
        }
        gnbaBuilder.addTransition(
            one.toString(), assumedAtomicPropositions.toString(), potentialSuccessor.toString());
      }
    }

    /*
     * We add the accepting sets for the GNBA.
     * The following additional accepting sets were derived
     * 1. F_{always phi} := {B in Q | always phi in B or phi not in B}
     * 2. F_{eventually phi} := {B in Q | phi in B or eventually phi not in B}
     */
    for (LTLFormula ltlFormula : closure) {
      if (ltlFormula instanceof LTLUntilFormula untilFormula) {
        Set<String> acceptingSet = new HashSet<>();

        for (Guess state : elementarySets) {
          if (!state.isAssumed(untilFormula) || state.isAssumed(untilFormula.getRight())) {
            acceptingSet.add(state.toString());
          }
        }

        gnbaBuilder.addAcceptingSet(acceptingSet);
      } else if (ltlFormula instanceof LTLEventuallyFormula eventuallyFormula) {
        Set<String> acceptingSet = new HashSet<>();
        for (Guess state : elementarySets) {
          if (!state.isAssumed(eventuallyFormula)
              || state.isAssumed(eventuallyFormula.getFormula())) {
            acceptingSet.add(state.toString());
          }
        }
        gnbaBuilder.addAcceptingSet(acceptingSet);
      } else if (ltlFormula instanceof LTLAlwaysFormula alwaysFormula) {
        Set<String> acceptingSet = new HashSet<>();
        for (Guess state : elementarySets) {
          if (state.isAssumed(alwaysFormula) || !state.isAssumed(alwaysFormula.getFormula())) {
            acceptingSet.add(state.toString());
          }
        }
        gnbaBuilder.addAcceptingSet(acceptingSet);
      }
    }

    for (Guess elementarySet : elementarySets) {
      if (elementarySet.isAssumed(formula)) {
        gnbaBuilder.addInitialState(elementarySet.toString());
      }
    }

    return gnbaBuilder.build();
  }

  // we can reduce the closure by removing everything that is directly implied by another formula
  public Set<LTLFormula> reduceClosure(Set<LTLFormula> closure) {
    Set<LTLFormula> reduced = new HashSet<>();
    for (LTLFormula ltlFormula : closure) {
      if (ltlFormula instanceof LTLTrueFormula
          || ltlFormula instanceof LTLFalseFormula
          || ltlFormula instanceof LTLNotFormula
          || ltlFormula instanceof LTLParenthesisFormula) {
        continue;
      }

      reduced.add(ltlFormula);
    }

    return reduced;
  }

  private Set<Guess> computeElementarySets(
      Set<String> atomicPropositions, Set<LTLFormula> closure) {
    Set<Guess> elementarySets = new HashSet<>();

    Assignment assignment = new Assignment();

    // todo: figure out what to do with this
    for (LTLFormula ltlFormula : closure) {
      if (ltlFormula instanceof LTLIdentifierFormula identifierFormula) {
        String identifier = identifierFormula.getIdentifier();
        if (!atomicPropositions.contains(identifier)) {
          throw new IllegalStateException(
              "Identifier '" + identifier + "' not in atomic propositions");
        }
      }
    }

    // add all atomic propositions to the closure, even if they don't occur in the formula
    for (String atomicProposition : atomicPropositions) {
      LTLFormula atomicPropositionFormula = identifier(atomicProposition);
      closure.add(atomicPropositionFormula);
    }

    Set<LTLFormula> reduced = reduceClosure(closure);

    while (true) {
      Optional<LTLFormula> unassigned =
          reduced.stream().filter(f -> !assignment.assignsFormulaOrNegation(f)).findFirst();
      if (unassigned.isEmpty()) {
        Guess guess = assignment.toB();
        if (guess.isElementary(closure)) {
          elementarySets.add(guess);
        }

        boolean success = assignment.backtrack();
        if (!success) {
          break;
        }
      } else {
        LTLFormula f = unassigned.get();
        assignment.assign(f, true);
      }
    }

    return elementarySets;
  }

  static class Guess {
    private final Set<LTLFormula> assumedSubformulas;

    Guess(Set<LTLFormula> assumedSubformulas) {
      this.assumedSubformulas = assumedSubformulas;
    }

    public Set<String> assumedAtomicPropositions() {
      Set<String> atomicPropositions = new HashSet<>();
      for (LTLFormula formula : assumedSubformulas) {
        if (formula instanceof LTLIdentifierFormula identifierFormula) {
          atomicPropositions.add(identifierFormula.getIdentifier());
        }
      }

      return atomicPropositions;
    }

    public boolean isAssumed(LTLFormula formula) {
      if (formula instanceof LTLTrueFormula) {
        return true;
      } else if (assumedSubformulas.contains(formula)) {
        return true;
      } else if (formula instanceof LTLNotFormula notFormula) {
        return !isAssumed(notFormula.getFormula());
      } else if (formula instanceof LTLParenthesisFormula parenthesisFormula) {
        return isAssumed(parenthesisFormula.getFormula());
      }

      return false;
    }

    public boolean isElementary(Set<LTLFormula> closure) {
      return isMaximallyConsistent(closure) && isLocallyConsistent(closure);
    }

    // we left out the rule for 'not' because by construction, we only have positive literals
    public boolean isMaximallyConsistent(Set<LTLFormula> closure) {
      for (LTLFormula formula : closure) {
        boolean lhs;
        boolean rhs;

        if (formula instanceof LTLTrueFormula
            || formula instanceof LTLFalseFormula
            || formula instanceof LTLIdentifierFormula
            || formula instanceof LTLParenthesisFormula
            || formula instanceof LTLNextFormula
            || formula instanceof LTLUntilFormula
            || formula instanceof LTLEventuallyFormula
            || formula instanceof LTLAlwaysFormula
            || formula instanceof LTLNotFormula) {
          continue;
        }

        if (formula instanceof LTLAndFormula andFormula) {
          lhs = isAssumed(andFormula);

          rhs = true;
          for (LTLFormula component : andFormula.getComponents()) {
            rhs &= isAssumed(component);
          }

          if (lhs != rhs) {
            return false;
          }
        } else if (formula instanceof LTLOrFormula orFormula) {
          lhs = isAssumed(orFormula);

          rhs = false;
          for (LTLFormula component : orFormula.getComponents()) {
            rhs |= isAssumed(component);
          }

          if (lhs != rhs) {
            return false;
          }
        } else if (formula instanceof LTLImplicationFormula implicationFormula) {
          lhs = isAssumed(implicationFormula);

          rhs =
              !isAssumed(implicationFormula.getLeft()) || isAssumed(implicationFormula.getRight());

          if (lhs != rhs) {
            return false;
          }
        } else {
          throw new UnsupportedOperationException("Unsupported formula: " + formula);
        }
      }

      return true;
    }

    /*
     * We derived the following additional rules for local consistency:
     * 1. phi in B => eventually phi in B
     * 2. always phi in B => phi in B
     */
    public boolean isLocallyConsistent(Set<LTLFormula> closure) {
      for (LTLFormula subformula : closure) {
        if (subformula instanceof LTLUntilFormula untilFormula) {
          boolean rightAssumed = isAssumed(untilFormula.getRight());
          boolean untilAssumed = isAssumed(untilFormula);

          if (rightAssumed && !untilAssumed) {
            return false;
          }

          boolean leftAssumed = isAssumed(untilFormula.getLeft());

          if (untilAssumed && !rightAssumed && !leftAssumed) {
            return false;
          }
        } else if (subformula instanceof LTLAlwaysFormula alwaysFormula) {
          boolean alwaysAssumed = isAssumed(alwaysFormula);
          boolean innerAssumed = isAssumed(alwaysFormula.getFormula());

          if (alwaysAssumed && !innerAssumed) {
            return false;
          }
        } else if (subformula instanceof LTLEventuallyFormula eventuallyFormula) {
          boolean eventuallyAssumed = isAssumed(eventuallyFormula);
          boolean innerAssumed = isAssumed(eventuallyFormula.getFormula());

          if (innerAssumed && !eventuallyAssumed) {
            return false;
          }
        }
      }

      return true;
    }

    @Override
    public String toString() {
      return assumedSubformulas.toString();
    }
  }

  static class Assignment {

    private final Map<LTLFormula, Boolean> assignments;
    private final Stack<SimpleEntry<LTLFormula, Boolean>> decisionStack;

    Assignment() {
      this.assignments = new HashMap<>();
      this.decisionStack = new Stack<>();
    }

    boolean assignsFormulaOrNegation(LTLFormula formula) {
      return assignments.containsKey(formula) || assignments.containsKey(formula.negate());
    }

    boolean get(LTLFormula formula) {
      return assignments.get(formula);
    }

    void assign(LTLFormula formula, boolean value) {
      assignments.put(formula, value);
      decisionStack.push(new SimpleEntry<>(formula, false));
    }

    private void reassign(LTLFormula formula, boolean value) {
      assignments.put(formula, value);
      decisionStack.push(new SimpleEntry<>(formula, true));
    }

    boolean backtrack() {
      while (!decisionStack.isEmpty()) {
        SimpleEntry<LTLFormula, Boolean> lastDecision = decisionStack.pop();
        if (lastDecision.getValue()) {
          assignments.remove(lastDecision.getKey());
        } else {
          reassign(lastDecision.getKey(), !get(lastDecision.getKey()));
          return true;
        }
      }

      return false;
    }

    Guess toB() {
      Set<LTLFormula> assumedSubformulas = new HashSet<>();
      for (LTLFormula formula : assignments.keySet()) {
        if (get(formula)) {
          assumedSubformulas.add(formula);
        }
      }

      return new Guess(assumedSubformulas);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for (SimpleEntry<LTLFormula, Boolean> decision : decisionStack) {
        boolean value = get(decision.getKey());
        int valueInt = value ? 1 : 0;
        sb.append(decision.getKey().toString()).append("=").append(valueInt).append("; ");
      }

      return sb.toString();
    }
  }
}
