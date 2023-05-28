package me.paultristanwagner.modelchecking.ts;

import java.util.*;

public class TransitionSystemBuilder {
  
  private final List<String> states;
  private final List<Transition> transitions;
  private final List<String> initialStates;
  private final List<String> atomicPropositions;
  private final Map<String, List<String>> labelingFunction;
  
  public TransitionSystemBuilder() {
    this.states = new ArrayList<>();
    this.transitions = new ArrayList<>();
    this.initialStates = new ArrayList<>();
    this.atomicPropositions = new ArrayList<>();
    this.labelingFunction = new HashMap<>();
  }
  
  public TransitionSystemBuilder addState( String state ) {
    this.states.add( state );
    return this;
  }
  
  public TransitionSystemBuilder addTransition( Transition transition ) {
    this.transitions.add( transition );
    return this;
  }
  
  public TransitionSystemBuilder addTransition(String from, String to) {
    this.transitions.add( Transition.of( from, to ) );
    return this;
  }
  
  public TransitionSystemBuilder addInitialState( String initialState ) {
    this.initialStates.add( initialState );
    return this;
  }
  
  public TransitionSystemBuilder setAtomicPropositions( String... atomicPropositions ) {
    return setAtomicPropositions( Arrays.asList( atomicPropositions ) );
  }
  
  public TransitionSystemBuilder setAtomicPropositions( List<String> atomicPropositions ) {
    this.atomicPropositions.clear();
    this.atomicPropositions.addAll( atomicPropositions );
    return this;
  }
  
  public TransitionSystemBuilder addAtomicProposition( String atomicProposition ) {
    this.atomicPropositions.add( atomicProposition );
    return this;
  }
  
  public TransitionSystemBuilder addLabel( String state, String atomicProposition ) {
    List<String> labels = labelingFunction.computeIfAbsent( state, k -> new ArrayList<>() );
    labels.add( atomicProposition );
    
    return this;
  }
  
  public TransitionSystemBuilder addLabels(String state, String... atomicPropositions) {
    List<String> labels = labelingFunction.computeIfAbsent( state, k -> new ArrayList<>() );
    labels.addAll( Arrays.asList( atomicPropositions ) );
    
    return this;
  }
  
  public TransitionSystem build() {
    return new TransitionSystem( states, transitions, initialStates, atomicPropositions, labelingFunction );
  }
}
