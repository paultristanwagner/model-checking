package me.paultristanwagner.modelchecking.ltl.formula;

public class LTLParenthesisFormula extends LTLFormula {

    private final LTLFormula formula;

    private LTLParenthesisFormula( LTLFormula formula ) {
        this.formula = formula;
    }

    public static LTLParenthesisFormula of( LTLFormula formula ) {
        return new LTLParenthesisFormula( formula );
    }

    public LTLFormula getFormula() {
        return formula;
    }

    @Override
    public String toString() {
        if (formula instanceof LTLUntilFormula || formula instanceof LTLParenthesisFormula) {
            return formula.toString();
        }

        return "(" + formula + ")";
    }
}
