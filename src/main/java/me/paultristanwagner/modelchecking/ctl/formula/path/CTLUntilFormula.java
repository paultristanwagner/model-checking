package me.paultristanwagner.modelchecking.ctl.formula.path;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;

import static me.paultristanwagner.modelchecking.ctl.parse.CTLLexer.UNTIL_SYMBOL;

public class CTLUntilFormula extends CTLPathFormula {

    private final CTLFormula left;
    private final CTLFormula right;

    private CTLUntilFormula(CTLFormula left, CTLFormula right) {
        this.left = left;
        this.right = right;
    }

    public static CTLUntilFormula of(CTLFormula left, CTLFormula right) {
        return new CTLUntilFormula(left, right);
    }

    public CTLFormula getLeft() {
        return left;
    }

    public CTLFormula getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + UNTIL_SYMBOL + " " + right.toString() + ")";
    }
}
