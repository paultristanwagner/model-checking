package me.paultristanwagner.modelchecking.ts;

public final class TSPersistenceResult {

    private final boolean isPersistent;
    private final InfinitePath witness;

    private TSPersistenceResult(boolean isPersistent, InfinitePath witness) {
        this.isPersistent = isPersistent;
        this.witness = witness;
    }

    public static TSPersistenceResult persistent() {
        return new TSPersistenceResult(true, null);
    }

    public static TSPersistenceResult notPersistent(InfinitePath witness) {
        return new TSPersistenceResult(false, witness);
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public InfinitePath getWitness() {
        return witness;
    }
}
