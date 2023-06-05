package me.paultristanwagner.modelchecking.ltl.formula;

import static me.paultristanwagner.modelchecking.util.Symbol.UNTIL_SYMBOL;

public class LTLUntilFormula extends LTLFormula {

    private final LTLFormula left;
    private final LTLFormula right;

    private LTLUntilFormula( LTLFormula left, LTLFormula right ) {
        this.left = left;
        this.right = right;
    }

    public static LTLFormula until(LTLFormula left, LTLFormula right ) {
        return new LTLUntilFormula( left, right );
    }

    public LTLFormula getLeft() {
        return left;
    }

    public LTLFormula getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + UNTIL_SYMBOL + " " + right + ")";
    }
}
