package me.paultristanwagner.modelchecking.ltl.formula;

public class LTLParenthesisFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLParenthesisFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLFormula of( LTLFormula formula ) {
        return new LTLParenthesisFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        return "(" + formula + ")";
    }
}
