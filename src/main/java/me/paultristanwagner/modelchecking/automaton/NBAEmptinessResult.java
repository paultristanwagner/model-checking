package me.paultristanwagner.modelchecking.automaton;

import me.paultristanwagner.modelchecking.ts.CyclePath;

public final class NBAEmptinessResult {
  private final boolean isEmpty;
  private final CyclePath witness;

  private NBAEmptinessResult(boolean isEmpty, CyclePath witness) {
    this.isEmpty = isEmpty;
    this.witness = witness;
  }

  public static NBAEmptinessResult empty() {
    return new NBAEmptinessResult(true, null);
  }

  public static NBAEmptinessResult nonEmpty(CyclePath witness) {
    return new NBAEmptinessResult(false, witness);
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public CyclePath getWitness() {
    return witness;
  }
}
