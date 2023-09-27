package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NOT_SYMBOL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLNotFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLNotFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLNotFormula not(LTLFormula formula ) {
        return new LTLNotFormula( formula );
    }

    @Override
    public List<LTLFormula> getAllSubformulas() {
        List<LTLFormula> subformulas = new ArrayList<>();
        subformulas.add( this );
        subformulas.addAll( formula.getAllSubformulas() );
        return subformulas;
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        if(formula instanceof LTLAndFormula || formula instanceof LTLOrFormula ) {
            return NOT_SYMBOL + "(" + formula + ")";
        }
        return NOT_SYMBOL + formula;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LTLNotFormula notFormula && formula.equals(notFormula.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formula);
    }
}
