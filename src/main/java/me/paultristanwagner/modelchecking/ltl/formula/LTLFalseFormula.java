package me.paultristanwagner.modelchecking.ltl.formula;

public class LTLFalseFormula extends LTLFormula {

    private LTLFalseFormula() {
    }

    public static LTLFalseFormula FALSE() {
        return new LTLFalseFormula();
    }

    @Override
    public String toString() {
        return "false";
    }
}
