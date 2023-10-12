package me.paultristanwagner.modelchecking;

import java.util.Set;
import me.paultristanwagner.modelchecking.automaton.State;
import me.paultristanwagner.modelchecking.ts.BasicTransitionSystem;

public interface ModelChecker<T extends Formula, R extends ModelCheckingResult> {

  R check(BasicTransitionSystem ts, T formula);

  Set<State> sat(BasicTransitionSystem ts, T formula);
}
