package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.ALWAYS_SYMBOL;

public class LTLAlwaysFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLAlwaysFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLFormula of( LTLFormula formula ) {
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
