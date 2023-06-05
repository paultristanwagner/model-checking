package me.paultristanwagner.modelchecking.ltl.formula;

import me.paultristanwagner.modelchecking.util.Symbol;

import static me.paultristanwagner.modelchecking.util.Symbol.NOT_SYMBOL;

public class LTLNotFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLNotFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLNotFormula of( LTLFormula formula ) {
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
