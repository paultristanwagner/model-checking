package me.paultristanwagner.modelchecking.ctl;

import me.paultristanwagner.modelchecking.ctl.formula.path.*;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;

import java.util.ArrayList;
import java.util.List;

import static me.paultristanwagner.modelchecking.ctl.formula.path.CTLAlwaysFormula.always;
import static me.paultristanwagner.modelchecking.ctl.formula.path.CTLNextFormula.next;
import static me.paultristanwagner.modelchecking.ctl.formula.path.CTLUntilFormula.until;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLAllFormula.forAll;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLAndFormula.and;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLExistsFormula.exists;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLNotFormula.not;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLOrFormula.or;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLParenthesisFormula.parenthesis;
import static me.paultristanwagner.modelchecking.ctl.formula.state.CTLTrueFormula.TRUE;

public class CTLENFConverter {

    // todo: improve code readability

    /**
     * <p>
     * Converts a CTL formula into an equivalent formula in ENF <br>
     * in which only existential quantifiers are allowed. <br>
     * Furthermore we only allow the box and the until operator. <br>
     * <br>
     * Logical equivalences used: <br>
     * ALL NEXT PHI = NOT EXISTS NEXT NOT PHI <br>
     * ALL PHI UNTIL PSI = NOT EXISTS (NOT PSI UNTIL (NOT PHI AND NOT PSI)) AND NOT EXISTS ALWAYS NOT PSI <br>
     * ALL ALWAYS PHI = NOT EXISTS EVENTUALLY NOT PHI <br>
     *
     * </p>
     *
     * @param formula the formula to convert
     * @return the converted formula
     */
    public CTLFormula convert(CTLFormula formula) {
        if (formula instanceof CTLExistsFormula existsFormula) {
            return convertExistsFormula(existsFormula);
        } else if (formula instanceof CTLAllFormula allFormula) {
            return convertAllFormula(allFormula);
        } else if (formula instanceof CTLIdentifierFormula) {
            return formula;
        } else if (formula instanceof CTLParenthesisFormula parenthesisFormula) {
            return parenthesis(convert(parenthesisFormula.getInner()));
        } else if (formula instanceof CTLTrueFormula || formula instanceof CTLFalseFormula) {
            return formula;
        } else if (formula instanceof CTLOrFormula orFormula) {
            List<CTLFormula> arguments = orFormula.getComponents();
            List<CTLFormula> convertedArguments = new ArrayList<>();
            for (CTLFormula argument : arguments) {
                convertedArguments.add(convert(argument));
            }
            return or(convertedArguments);
        } else if (formula instanceof CTLAndFormula andFormula) {
            List<CTLFormula> arguments = andFormula.getComponents();
            List<CTLFormula> convertedArguments = new ArrayList<>();
            for (CTLFormula argument : arguments) {
                convertedArguments.add(convert(argument));
            }
            return and(convertedArguments);
        } else if (formula instanceof CTLNotFormula notFormula) {
            return not(convert(notFormula.getArgument()));
        }

        throw new UnsupportedOperationException();
    }

    private CTLFormula convertExistsFormula(CTLExistsFormula formula) {
        CTLPathFormula pathFormula = formula.getPathFormula();
        if (pathFormula instanceof CTLAlwaysFormula alwaysFormula) {
            return exists(always(convert(alwaysFormula.getStateFormula())));
        } else if (pathFormula instanceof CTLEventuallyFormula eventuallyFormula) {
            // EXISTS EVENTUALLY PHI = EXISTS (TRUE UNTIL PHI)
            CTLUntilFormula untilFormula = until(TRUE(), eventuallyFormula.getStateFormula());
            return convert(exists(untilFormula));
        } else if (pathFormula instanceof CTLNextFormula nextFormula) {
            return exists(next(convert(nextFormula.getStateFormula())));
        } else if (pathFormula instanceof CTLUntilFormula untilFormula) {
            return exists(until(convert(untilFormula.getLeft()), convert(untilFormula.getRight())));
        }

        throw new UnsupportedOperationException();
    }

    private CTLFormula convertAllFormula(CTLAllFormula formula) {
        CTLPathFormula pathFormula = formula.getPathFormula();
        if (pathFormula instanceof CTLNextFormula nextFormula) {

            CTLFormula stateFormula = nextFormula.getStateFormula();
            return not(exists(next(not(convert(stateFormula)))));
        } else if (pathFormula instanceof CTLUntilFormula untilFormula) {
            CTLFormula phi = untilFormula.getLeft();
            CTLFormula psi = untilFormula.getRight();

            CTLFormula phiConverted = convert(phi);
            CTLFormula psiConverted = convert(psi);

            CTLFormula notPhiConverted = not(phiConverted);
            CTLFormula notPsiConverted = not(psiConverted);

            CTLFormula notPhiAndNotPsi = and(notPhiConverted, notPsiConverted);

            CTLFormula left = not(exists(until(notPsiConverted, notPhiAndNotPsi)));
            CTLFormula right = not(exists(always(notPsiConverted)));

            return and(left, right);
        } else if (pathFormula instanceof CTLAlwaysFormula alwaysFormula) {
            CTLFormula stateFormula = alwaysFormula.getStateFormula();
            CTLFormula stateFormulaConverted = convert(stateFormula);

            // ALL ALWAYS PHI = NOT EXISTS EVENTUALLY NOT PHI = NOT EXISTS (TRUE UNTIL NOT PHI)
            CTLFormula notConvertedStateFormula = not(stateFormulaConverted);
            CTLUntilFormula untilFormula = until(TRUE(), notConvertedStateFormula);
            CTLExistsFormula existsFormula = exists(untilFormula);

            return not(existsFormula);
        } else if (pathFormula instanceof CTLEventuallyFormula eventuallyFormula) {
            CTLFormula stateFormula = eventuallyFormula.getStateFormula();

            // ALL EVENTUALLY PHI = ALL (TRUE UNTIL PHI)

            CTLUntilFormula untilFormula = until(TRUE(), stateFormula);
            CTLAllFormula allFormula = forAll(untilFormula);

            return convertAllFormula(allFormula);
        }

        throw new UnsupportedOperationException();
    }
}
