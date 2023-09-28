package me.paultristanwagner.modelchecking.ts;

public final class TSPersistenceResult {

  private final boolean isPersistent;
  private final CyclePath witness;

  private TSPersistenceResult(boolean isPersistent, CyclePath witness) {
    this.isPersistent = isPersistent;
    this.witness = witness;
  }

  public static TSPersistenceResult persistent() {
    return new TSPersistenceResult(true, null);
  }

  public static TSPersistenceResult notPersistent(CyclePath witness) {
    return new TSPersistenceResult(false, witness);
  }

  public boolean isPersistent() {
    return isPersistent;
  }

  public CyclePath getWitness() {
    return witness;
  }
}
