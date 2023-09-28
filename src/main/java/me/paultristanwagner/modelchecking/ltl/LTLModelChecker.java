package me.paultristanwagner.modelchecking.ltl;

import java.util.Set;
import me.paultristanwagner.modelchecking.ModelChecker;
import me.paultristanwagner.modelchecking.ltl.formula.LTLFormula;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public interface LTLModelChecker extends ModelChecker<LTLFormula, LTLModelCheckingResult> {

  Set<String> sat(TransitionSystem ts, LTLFormula formula);
}
