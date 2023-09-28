package me.paultristanwagner.modelchecking.ctlstar;

import me.paultristanwagner.modelchecking.ModelCheckingResult;

public class CTLStarModelCheckingResult extends ModelCheckingResult {

  private CTLStarModelCheckingResult(boolean models) {
    super(models);
  }

  public static CTLStarModelCheckingResult models() {
    return new CTLStarModelCheckingResult(true);
  }

  public static CTLStarModelCheckingResult doesNotModel() {
    return new CTLStarModelCheckingResult(false);
  }
}
