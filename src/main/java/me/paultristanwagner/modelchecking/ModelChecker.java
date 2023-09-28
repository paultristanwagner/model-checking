package me.paultristanwagner.modelchecking;

import java.util.Set;
import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public interface ModelChecker<T extends Formula, R extends ModelCheckingResult> {

  R check(TransitionSystem ts, T formula);

  Set<String> sat(TransitionSystem ts, T formula);
}
