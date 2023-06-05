package me.paultristanwagner.modelchecking.ltl.formula;

public class LTLIdentifierFormula extends LTLFormula {

    private final String identifier;

    private LTLIdentifierFormula( String identifier ) {
        this.identifier = identifier;
    }

    public static LTLIdentifierFormula of( String identifier ) {
        return new LTLIdentifierFormula( identifier );
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
