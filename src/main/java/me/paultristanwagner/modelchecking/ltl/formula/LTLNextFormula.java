package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.NEXT_SYMBOL;

public class LTLNextFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLNextFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLNextFormula next(LTLFormula formula ) {
        return new LTLNextFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return NEXT_SYMBOL + formula;
    }
}
