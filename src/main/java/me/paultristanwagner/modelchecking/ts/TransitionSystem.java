package me.paultristanwagner.modelchecking.ts;

import com.google.gson.*;
import me.paultristanwagner.modelchecking.ts.Transition.TransitionAdapter;

import java.util.List;
import java.util.Map;

public class TransitionSystem {

  private static final Gson GSON;

  static {
    GSON =
        new GsonBuilder()
            .registerTypeAdapter(Transition.class, new TransitionAdapter())
            .create();
  }

  private final List<String> states;
  private final List<Transition> transitions;
  private final List<String> initialStates;
  private final List<String> atomicPropositions;
  private final Map<String, List<String>> labelingFunction;

  public TransitionSystem(
      List<String> states,
      List<Transition> transitions,
      List<String> initialStates,
      List<String> atomicPropositions,
      Map<String, List<String>> labelingFunction) {
    this.states = states;
    this.transitions = transitions;
    this.initialStates = initialStates;
    this.atomicPropositions = atomicPropositions;
    this.labelingFunction = labelingFunction;

    for (String state : states) {
      labelingFunction.putIfAbsent(state, List.of());
    }
  }

  @Override
  public String toString() {
    return toJson();
  }

  public List<String> getStates() {
    return states;
  }

  public List<String> getInitialStates() {
    return initialStates;
  }

  // todo: low hanging fruit: precompute successors
  public List<String> getSuccessors(String state) {
    return transitions.stream()
        .filter(transition -> transition.getFrom().equals(state))
        .map(Transition::getTo)
        .toList();
  }

  public List<String> getLabel(String state) {
    return labelingFunction.get(state);
  }

  public String toJson() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    builder.append("\n");
    builder.append("\t");
    builder.append("\"states\": ");
    builder.append( GSON.toJson( states ) );
    builder.append(",\n");
    builder.append("\t");
    builder.append("\"transitions\": [\n");
    for (int i = 0; i < transitions.size(); i++) {
      Transition transition = transitions.get(i);
      builder.append("\t\t");
      builder.append( GSON.toJson( transition ) );
      if (i < transitions.size() - 1) {
        builder.append(", ");
      }
      builder.append("\n");
    }
    
    builder.append( "\t],\n" );
    builder.append( "\t" );
    builder.append( "\"initialStates\": " );
    builder.append( GSON.toJson( initialStates ) );
    builder.append( ",\n" );
    
    builder.append( "\t" );
    builder.append( "\"atomicPropositions\": " );
    builder.append( GSON.toJson( atomicPropositions ) );
    builder.append( ",\n" );
    
    builder.append( "\t" );
    builder.append( "\"labelingFunction\": {\n" );
    for (int i = 0; i < states.size(); i++) {
      String state = states.get(i);
      List<String> labels = labelingFunction.get(state);

      builder.append( "\t\t" );
      builder.append( "\"" );
      builder.append( state );
      builder.append( "\": " );
      builder.append( GSON.toJson( labels ) );

      if (i < states.size() - 1) {
        builder.append(", ");
      }
      builder.append("\n");
    }
    
    builder.append( "\t" );
    builder.append( "}\n" );
    builder.append( "}" );
    
    return builder.toString();
  }

  public static TransitionSystem fromJson(String string) {
    return GSON.fromJson( string, TransitionSystem.class );
  }
}
