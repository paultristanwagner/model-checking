package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTLIdentifierFormula extends LTLFormula {

    private final String identifier;

    private LTLIdentifierFormula( String identifier ) {
        this.identifier = identifier;
    }

    public static LTLIdentifierFormula identifier(String identifier ) {
        return new LTLIdentifierFormula( identifier );
    }

    @Override
    public List<LTLFormula> getAllSubformulas() {
        List<LTLFormula> subformulas = new ArrayList<>();
        subformulas.add( this );
        return subformulas;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LTLIdentifierFormula identifierFormula && identifier.equals(identifierFormula.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
