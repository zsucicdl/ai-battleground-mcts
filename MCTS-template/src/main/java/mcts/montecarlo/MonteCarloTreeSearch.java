package mcts.montecarlo;

import mcts.game.*;
import mcts.tree.Node;
import mcts.tree.Tree;

import java.util.HashMap;
import java.util.List;

public class MonteCarloTreeSearch {
    private static final int SIMULATION_DEPTH = 10;

    public MonteCarloTreeSearch() {
    }

    public Move findNextMove(Board board) {
        long start = System.currentTimeMillis();
        long end = start + 3000; // TODO set time limit
        int myPlayerId = board.getCurrentPlayerIndex();

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
            int score = simulateRandomPlayout(nodeToExplore, myPlayerId);
            // Phase 4 - Update
            backPropogation(nodeToExplore, score);
        }

        Node winnerNode = rootNode.getChildWithMaxVisits();
        return winnerNode.getState().getInitialMove();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node != null && node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        for (State state : possibleStates) {
            Node newNode = new Node(state);
            newNode.setParent(node);
            node.getChildArray().add(newNode);
        }
    }

    private int simulateRandomPlayout(Node node, int myPlayerId) {
        Node tempNode = node.copy();
        Board tempBoard = tempNode.getState().getBoard();
        int score = 0;

        // TODO implement simulation
        for(int i = 0; i < SIMULATION_DEPTH; i++) {
            Move randomMove = tempBoard.getRandomMove();
            int currentPlayerId = tempBoard.getCurrentPlayerIndex();
            tempBoard.playMove(randomMove);
            if(currentPlayerId == myPlayerId){
                score += scoreFunction(tempBoard, randomMove);
            }
            if(!tempBoard.isRunning()){
                break;
            }
        }
        return score;
    }

    private int scoreFunction(Board board, Move randomMove) {
        int score = 0;
        if(randomMove.getType() == MoveType.INITIAL){
            score += initialMoveScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.BUILD_TOWN){
            score += 100;
            score += buildTownScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.UPGRADE_TOWN){
            score += 80;
            score += buildTownScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.BUILD_ROAD){
            score += 3;
        } else if(randomMove.getType() == MoveType.MOVE){
            score += 1;
        }
        score += board.getCurrentPlayer().getNoOfResources() / 100;
        return score;
    }

    private int buildRoadScoreFunction(Board board, Move randomMove) {
        int noOfCities = board.getIndexCities()[board.getCurrentPlayerIndex()].size();
        int noOfRoads = 0;
        for (Integer i : board.getIndexXYRoads().values()) {
            if(i == board.getCurrentPlayerIndex())
                noOfRoads++;
        }
        return noOfCities * (25 - noOfRoads);
    }

    private int buildTownScoreFunction(Board board, Move randomMove) {
        int score = 0;
        for(Field f : board.getCurrentPlayer().getCurrentIntersection().getAdjacentFields()){
            score += f.getWeight();
        }
        return score;
    }

    private int initialMoveScoreFunction(Board board, Move randomMove) {
        int score = 0;
        if(board.getTurns() <= 1){
            for(Field f : board.getIndexIntersections().get(randomMove.getIndex1()).getAdjacentFields()){
                switch (f.getResource()){
                    case WOOD:
                    case CLAY:
                        score += f.getWeight() * 4;
                        break;
                    case WHEAT:
                    case SHEEP:
                        score += f.getWeight() * 3;
                }
            }
        } else {
            HashMap<Resource, Boolean> flags = new HashMap<>();
            for(City c : board.getIndexCities()[board.getCurrentPlayerIndex()].values()){
                for(Field f : c.getIntersection().getAdjacentFields()){
                    if(f.getResource() == Resource.WOOD || f.getResource() == Resource.CLAY || f.getResource() == Resource.SHEEP || f.getResource() == Resource.WHEAT){
                        flags.put(f.getResource(), true);
                    }
                }
            }
            for(Field f : board.getIndexIntersections().get(randomMove.getIndex1()).getAdjacentFields()){
                switch (f.getResource()){
                    case WOOD:
                        flags.put(Resource.WOOD, true);
                        score += f.getWeight() * 4;
                        break;
                    case CLAY:
                        flags.put(Resource.CLAY, true);
                        score += f.getWeight() * 4;
                        break;
                    case WHEAT:
                        flags.put(Resource.WHEAT, true);
                        score += f.getWeight() * 3;
                        break;
                    case SHEEP:
                        flags.put(Resource.SHEEP, true);
                        score += f.getWeight() * 3;
                }
            }
            if(flags.keySet().size() < 4){
                score = Integer.MIN_VALUE;
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
