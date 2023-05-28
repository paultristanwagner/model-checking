package me.paultristanwagner.modelchecking.ctl;

import me.paultristanwagner.modelchecking.ctl.formula.path.*;
import me.paultristanwagner.modelchecking.ctl.formula.state.*;

import java.util.ArrayList;
import java.util.List;

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
            return CTLParenthesisFormula.of(convert(parenthesisFormula.getInner()));
        } else if (formula instanceof CTLTrueFormula || formula instanceof CTLFalseFormula) {
            return formula;
        } else if (formula instanceof CTLOrFormula orFormula) {
            List<CTLFormula> arguments = orFormula.getComponents();
            List<CTLFormula> convertedArguments = new ArrayList<>();
            for (CTLFormula argument : arguments) {
                convertedArguments.add(convert(argument));
            }
            return CTLOrFormula.of(convertedArguments);
        } else if (formula instanceof CTLAndFormula andFormula) {
            List<CTLFormula> arguments = andFormula.getComponents();
            List<CTLFormula> convertedArguments = new ArrayList<>();
            for (CTLFormula argument : arguments) {
                convertedArguments.add(convert(argument));
            }
            return CTLAndFormula.of(convertedArguments);
        } else if (formula instanceof CTLNotFormula notFormula) {
            return CTLNotFormula.of(convert(notFormula.getArgument()));
        }

        throw new UnsupportedOperationException();
    }

    private CTLFormula convertExistsFormula(CTLExistsFormula formula) {
        CTLPathFormula pathFormula = formula.getPathFormula();
        if (pathFormula instanceof CTLAlwaysFormula alwaysFormula) {
            return CTLExistsFormula.of(CTLAlwaysFormula.of(convert(alwaysFormula.getStateFormula())));
        } else if (pathFormula instanceof CTLEventuallyFormula eventuallyFormula) {
            // EXISTS EVENTUALLY PHI = EXISTS (TRUE UNTIL PHI)
            CTLUntilFormula untilFormula =
                    CTLUntilFormula.of(new CTLTrueFormula(), eventuallyFormula.getStateFormula());
            return convert(CTLExistsFormula.of(untilFormula));
        } else if (pathFormula instanceof CTLNextFormula nextFormula) {
            return CTLExistsFormula.of(CTLNextFormula.of(convert(nextFormula.getStateFormula())));
        } else if (pathFormula instanceof CTLUntilFormula untilFormula) {
            return CTLExistsFormula.of(
                    CTLUntilFormula.of(convert(untilFormula.getLeft()), convert(untilFormula.getRight())));
        }

        throw new UnsupportedOperationException();
    }

    private CTLFormula convertAllFormula(CTLAllFormula formula) {
        CTLPathFormula pathFormula = formula.getPathFormula();
        if (pathFormula instanceof CTLNextFormula nextFormula) {

            CTLFormula stateFormula = nextFormula.getStateFormula();
            return CTLNotFormula.of(
                    CTLExistsFormula.of(CTLNextFormula.of(CTLNotFormula.of(convert(stateFormula)))));
        } else if (pathFormula instanceof CTLUntilFormula untilFormula) {
            CTLFormula phi = untilFormula.getLeft();
            CTLFormula psi = untilFormula.getRight();

            CTLFormula phiConverted = convert(phi);
            CTLFormula psiConverted = convert(psi);

            CTLFormula notPhiConverted = CTLNotFormula.of(phiConverted);
            CTLFormula notPsiConverted = CTLNotFormula.of(psiConverted);

            CTLFormula notPhiAndNotPsi = CTLAndFormula.of(notPhiConverted, notPsiConverted);

            CTLFormula left =
                    CTLNotFormula.of(
                            CTLExistsFormula.of(CTLUntilFormula.of(notPsiConverted, notPhiAndNotPsi)));
            CTLFormula right =
                    CTLNotFormula.of(CTLExistsFormula.of(CTLAlwaysFormula.of(notPsiConverted)));

            return CTLAndFormula.of(left, right);
        } else if (pathFormula instanceof CTLAlwaysFormula alwaysFormula) {
            CTLFormula stateFormula = alwaysFormula.getStateFormula();
            CTLFormula stateFormulaConverted = convert(stateFormula);

            // ALL ALWAYS PHI = NOT EXISTS EVENTUALLY NOT PHI = NOT EXISTS (TRUE UNTIL NOT PHI)
            CTLFormula notConvertedStateFormula = CTLNotFormula.of(stateFormulaConverted);
            CTLUntilFormula untilFormula =
                    CTLUntilFormula.of(new CTLTrueFormula(), notConvertedStateFormula);
            CTLExistsFormula existsFormula = CTLExistsFormula.of(untilFormula);

            return CTLNotFormula.of(existsFormula);
        } else if (pathFormula instanceof CTLEventuallyFormula eventuallyFormula) {
            CTLFormula stateFormula = eventuallyFormula.getStateFormula();

            // ALL EVENTUALLY PHI = ALL (TRUE UNTIL PHI)

            CTLUntilFormula untilFormula = CTLUntilFormula.of(new CTLTrueFormula(), stateFormula);
            CTLAllFormula allFormula = CTLAllFormula.of(untilFormula);

            return convertAllFormula(allFormula);
        }

        throw new UnsupportedOperationException();
    }
}
