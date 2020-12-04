package mcts.tree;

import mcts.montecarlo.State;

import java.util.*;

public class Node {
    State state;
    Node parent;
    List<Node> childArray;

    public Node() {
        this.state = new State();
        childArray = new ArrayList<Node>();
    }

    public Node(State state) {
        this.state = state;
        childArray = new ArrayList<Node>();
    }

    public Node(State state, Node parent, List<Node> childArray) {
        this.state = state;
        this.parent = parent;
        this.childArray = childArray;
    }

    public Node copy(){
        Node newNode = new Node();
        newNode.setState(this.state.copy());
        newNode.setParent(this.parent);
        for(Node childNode : this.childArray) {
            newNode.addChild(childNode);
        }
        return newNode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildArray() {
        return childArray;
    }

    public void setChildArray(List<Node> childArray) {
        this.childArray = childArray;
    }

    public void addChild(Node childNode) {
        this.childArray.add(childNode);
    }

    public Node getRandomChildNode() {
        int noOfPossibleMoves = this.childArray.size();
        int selectRandom = (int) (Math.random() * noOfPossibleMoves);
        return this.childArray.get(selectRandom);
    }

    public Node getChildWithMaxScore() {
        return this.childArray.stream().max(Comparator.comparing(c -> c.getState().getVisitCount())).get();
    }

}
