package me.paultristanwagner.modelchecking.ltl.formula;

import me.paultristanwagner.modelchecking.Formula;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.paultristanwagner.modelchecking.ltl.formula.LTLNotFormula.not;

public abstract class LTLFormula extends Formula {

    public abstract List<LTLFormula> getAllSubformulas();

    public LTLFormula negate() {
        if (this instanceof LTLNotFormula notFormula) {
            return notFormula.getFormula();
        } else {
            return not(this);
        }
    }

    public Set<LTLFormula> getClosure() {
        List<LTLFormula> subformulaList = getAllSubformulas();
        Set<LTLFormula> subformulaSet = new HashSet<>(subformulaList);
        Set<LTLFormula> closure = new HashSet<>(subformulaSet);
        for (LTLFormula formula : subformulaSet) {
            closure.add(formula.negate());
        }

        return closure;
    }
}
