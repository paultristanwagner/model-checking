package me.paultristanwagner.modelchecking.automaton;

public final class NBAEmptinessResult {
    private final boolean isEmpty;
    private final NBAEmptinessWitness witness;

    private NBAEmptinessResult(boolean isEmpty, NBAEmptinessWitness witness) {
        this.isEmpty = isEmpty;
        this.witness = witness;
    }

    public static NBAEmptinessResult empty() {
        return new NBAEmptinessResult(true, null);
    }

    public static NBAEmptinessResult nonEmpty(NBAEmptinessWitness witness) {
        return new NBAEmptinessResult(false, witness);
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public NBAEmptinessWitness witness() {
        return witness;
    }

    public String getPathDescription() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < witness.start().size(); i++) {
            sb.append(witness.start().get(i));
            sb.append(" ");
        }

        sb.append("(");
        for (int i = 0; i < witness.cycle().size() - 1; i++) {
            sb.append(witness.cycle().get(i));
            if(i < witness.cycle().size() - 2) {
                sb.append(" ");
            }
        }

        sb.append(")^ω");

        return sb.toString();
    }
}
