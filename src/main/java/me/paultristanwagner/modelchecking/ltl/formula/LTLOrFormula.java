package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.List;

import static me.paultristanwagner.modelchecking.util.Symbol.OR_SYMBOL;

public class LTLOrFormula extends LTLFormula {

    private final List<LTLFormula> components;

    private LTLOrFormula(List<LTLFormula> components) {
        this.components = components;
    }

    public static LTLOrFormula or(LTLFormula... components) {
        return new LTLOrFormula( List.of( components ) );
    }

    public static LTLOrFormula or(List<LTLFormula> components) {
        return new LTLOrFormula( components );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            builder.append( components.get( i ).toString() );

            if (i < components.size() - 1) {
                builder.append( " " );
                builder.append( OR_SYMBOL );
                builder.append( " " );
            }
        }

        return builder.toString();
    }
}
