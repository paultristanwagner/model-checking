package me.paultristanwagner.modelchecking.ltl;

import me.paultristanwagner.modelchecking.ModelCheckingResult;
import me.paultristanwagner.modelchecking.ts.CyclePath;

public class LTLModelCheckingResult extends ModelCheckingResult {

  private final CyclePath counterExample;

  private LTLModelCheckingResult(boolean result, CyclePath counterExample) {
    super(result);
    this.counterExample = counterExample;
  }

  public static LTLModelCheckingResult models() {
    return new LTLModelCheckingResult(true, null);
  }

  public static LTLModelCheckingResult doesNotModel(CyclePath counterExample) {
    return new LTLModelCheckingResult(false, counterExample);
  }

  public CyclePath getCounterExample() {
    return counterExample;
  }
}
