package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.ALWAYS_SYMBOL;

public class LTLAlwaysFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLAlwaysFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLAlwaysFormula of( LTLFormula formula ) {
        return new LTLAlwaysFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return ALWAYS_SYMBOL + formula;
    }
}
