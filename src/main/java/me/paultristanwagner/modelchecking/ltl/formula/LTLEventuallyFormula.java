package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.EVENTUALLY_SYMBOL;

public class LTLEventuallyFormula extends LTLFormula {

private final LTLFormula formula;

    private LTLEventuallyFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLEventuallyFormula of( LTLFormula formula ) {
        return new LTLEventuallyFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return EVENTUALLY_SYMBOL + formula;
    }
}
