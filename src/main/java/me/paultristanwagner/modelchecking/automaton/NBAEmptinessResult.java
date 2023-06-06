package me.paultristanwagner.modelchecking.automaton;

import me.paultristanwagner.modelchecking.ts.InfinitePath;

public final class NBAEmptinessResult {
    private final boolean isEmpty;
    private final InfinitePath witness;

    private NBAEmptinessResult(boolean isEmpty, InfinitePath witness) {
        this.isEmpty = isEmpty;
        this.witness = witness;
    }

    public static NBAEmptinessResult empty() {
        return new NBAEmptinessResult(true, null);
    }

    public static NBAEmptinessResult nonEmpty(InfinitePath witness) {
        return new NBAEmptinessResult(false, witness);
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public InfinitePath getWitness() {
        return witness;
    }
}
