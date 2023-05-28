package me.paultristanwagner.modelchecking.ctl;

import me.paultristanwagner.modelchecking.ctl.formula.state.CTLFormula;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public interface CTLModelChecker {
  
  boolean check( TransitionSystem ts, CTLFormula formula );
}
