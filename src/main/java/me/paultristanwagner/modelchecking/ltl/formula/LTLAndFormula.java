package me.paultristanwagner.modelchecking.ltl.formula;

import java.util.Arrays;
import java.util.List;

import static me.paultristanwagner.modelchecking.util.Symbol.AND_SYMBOL;

public class LTLAndFormula extends LTLFormula {

    private final List<LTLFormula> components;

    private LTLAndFormula(List<LTLFormula> components) {
        this.components = components;
    }

    public static LTLAndFormula and(LTLFormula... components) {
        return new LTLAndFormula(Arrays.asList(components));
    }

    public static LTLAndFormula and(List<LTLFormula> components) {
        return new LTLAndFormula(components);
    }

    public List<LTLFormula> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            builder.append(components.get(i).toString());

            if (i < components.size() - 1) {
                builder.append(" ");
                builder.append(AND_SYMBOL);
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}
