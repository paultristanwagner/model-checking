package me.paultristanwagner.modelchecking.ltl;

import me.paultristanwagner.modelchecking.ModelCheckingResult;
import me.paultristanwagner.modelchecking.ts.InfinitePath;

public class LTLModelCheckingResult extends ModelCheckingResult {

  private final InfinitePath counterExample;

  private LTLModelCheckingResult(boolean result, InfinitePath counterExample) {
    super(result);
    this.counterExample = counterExample;
  }

  public static LTLModelCheckingResult models() {
    return new LTLModelCheckingResult(true, null);
  }

  public static LTLModelCheckingResult doesNotModel(InfinitePath counterExample) {
    return new LTLModelCheckingResult(false, counterExample);
  }

  public InfinitePath getCounterExample() {
    return counterExample;
  }
}
