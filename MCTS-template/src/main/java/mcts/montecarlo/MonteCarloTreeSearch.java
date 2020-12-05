package mcts.montecarlo;

import mcts.game.Board;
import mcts.tree.Node;
import mcts.tree.Tree;

import java.util.List;

public class MonteCarloTreeSearch {
    private static final int SIMULATION_DEPTH = 10;

    private int opponent;
    private int playerNo;

    public MonteCarloTreeSearch() {
    }

    public Board findNextMove(Board board) {
        long start = System.currentTimeMillis();
        long end = start + 4800; // TODO set time limit

        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
            // Phase 2 - Expansion
            if (promisingNode.getState().getBoard().isRunning())
                expandNode(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int score = simulateRandomPlayout(nodeToExplore);
            // Phase 4 - Update
            backPropogation(nodeToExplore, score);
        }

        Node winnerNode = rootNode.getChildWithMaxVisits();
        return winnerNode.getState().getBoard();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            node.getChildArray().add(newNode);
        });
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = node.copy();
        State tempState = tempNode.getState();
        int score = 0;

        // TODO implement simulation
        for(int i = 0; i < SIMULATION_DEPTH; i++) {
            tempState.randomPlay();
            if(tempState.getBoard().isRunning()){
                break;
            }
        }
        return score;
    }

    private void backPropogation(Node nodeToExplore, int score) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            tempNode.getState().addScore(score);
            tempNode = tempNode.getParent();
        }
    }
}
