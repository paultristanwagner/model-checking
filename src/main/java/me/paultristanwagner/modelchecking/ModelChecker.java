package me.paultristanwagner.modelchecking;

import me.paultristanwagner.modelchecking.ts.TransitionSystem;

public interface ModelChecker<T extends Formula, R extends ModelCheckingResult> {

  R check(TransitionSystem ts, T formula);
}
