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

    public Board findNextMove(Board board, int playerNo) {
        long start = System.currentTimeMillis();
        long end = start + 200; // TODO set time limit

        this.playerNo = playerNo;
        this.opponent = 3 - playerNo;
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
            // Phase 2 - Expansion
            if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS)
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

        Node winnerNode = rootNode.getChildWithMaxScore();
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
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        });
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = node.copy();
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().checkStatus();

        // TODO implement hard punish for loss
        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }

        // TODO implement simulation
        for(int i = 0; i < SIMULATION_DEPTH; i++) {
            tempState.togglePlayer();
            tempState.randomPlay();

            boardStatus = tempState.getBoard().checkStatus();
            if(boardStatus != Board.IN_PROGRESS){
                break;
            }
        }
        return boardStatus;
    }

    private void backPropogation(Node nodeToExplore, int score) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            // TODO implement punish/reward function
            tempNode = tempNode.getParent();
        }
    }

    public static void main(String[] args) {
        Board board = new Board();
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();

        for(int i = 0; i < 9; i++) {
            board = mcts.findNextMove(board, i % 2 + 1);
            board.printBoard();
        }
    }
}
