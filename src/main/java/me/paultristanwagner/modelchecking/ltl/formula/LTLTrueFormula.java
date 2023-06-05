package me.paultristanwagner.modelchecking.ltl.formula;

public class LTLTrueFormula extends LTLFormula {

    private LTLTrueFormula() {
    }

    public static LTLTrueFormula TRUE() {
        return new LTLTrueFormula();
    }

    @Override
    public String toString() {
        return "true";
    }
}
