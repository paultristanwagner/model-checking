package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.NOT_SYMBOL;

public class LTLNotFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLNotFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLFormula of( LTLFormula formula ) {
        return new LTLNotFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return NOT_SYMBOL + formula;
    }
}
