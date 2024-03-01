package com.tsm4j.core.statetypes;

public interface StateType {

     Transition TRANSITION = Transition.INSTANCE;
     Output OUTPUT = Output.INSTANCE;
     Leaf LEAF = Leaf.INSTANCE;

     boolean isTransition();

     boolean isOutput();

     boolean isLeaf();
}