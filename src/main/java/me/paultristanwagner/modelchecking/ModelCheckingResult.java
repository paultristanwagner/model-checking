package me.paultristanwagner.modelchecking;

public abstract class ModelCheckingResult {

  protected final boolean models;

  public ModelCheckingResult(boolean models) {
    this.models = models;
  }

  public boolean isModel() {
    return models;
  }
}
