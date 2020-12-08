package mcts.montecarlo;

import mcts.game.Board;
import mcts.game.Field;
import mcts.game.Move;
import mcts.game.MoveType;
import mcts.tree.Node;
import mcts.tree.Tree;

import java.util.List;

public class MonteCarloTreeSearch {
    private static final int SIMULATION_DEPTH = 10;

    public MonteCarloTreeSearch() {
    }

    public Move findNextMove(Board board) throws InterruptedException {
        long start = System.currentTimeMillis();
        long end = start + 2500; // TODO set time limit
        int myPlayerId = board.getCurrentPlayerIndex();

        if(board.getTurns() < 4){
            return board.getBestInitialMove();
        }

        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);

        System.out.println("possible moves: " + board.getLegalMoves().size());

        while (System.currentTimeMillis() < end) {
            //TimeUnit.MILLISECONDS.sleep(100);
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
            double score = simulateRandomPlayout(nodeToExplore, myPlayerId);
            // Phase 4 - Update
            backPropagation(nodeToExplore, score);
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

    private double simulateRandomPlayout(Node node, int myPlayerId) {
        Node tempNode = node.copy();
        Board tempBoard = tempNode.getState().getBoard();
        double score = scoreFunction(tempBoard, tempNode.getState().getInitialMove());
        if(tempBoard.getTurns() < 4){
            return score;
        }
        for(int i = 0; i < SIMULATION_DEPTH; i++) {
            Move randomMove = tempBoard.getRandomMove();
            int currentPlayerId = tempBoard.getCurrentPlayerIndex();
            tempBoard.playMove(randomMove);
            if(currentPlayerId == myPlayerId){
                score += scoreFunction(tempBoard, myPlayerId);
            }
            if(!tempBoard.isRunning()){
                break;
            }
        }
        return score;
    }

    private double scoreFunction(Board board, int playerId){
        return board.getPlayers()[playerId].getPoints();
    }

    private double scoreFunction(Board board, Move randomMove) {
        if(randomMove.getType() == MoveType.BUILD_TOWN || randomMove.getType() == MoveType.UPGRADE_TOWN){
            return 1.0;
        } else {
            return -0.1;
        }
        /*
        double score = -1;
        if(randomMove.getType() == MoveType.INITIAL){
            score += initialMoveScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.BUILD_TOWN){
            score += 100;
            //score += buildTownScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.UPGRADE_TOWN){
            score += 80;
            //score += buildTownScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.BUILD_ROAD){
            score += 30;
        } else if(randomMove.getType() == MoveType.MOVE){
            score += 1;
        }
        return score / 100;
         */
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

    private double initialMoveScoreFunction(Board board, Move randomMove){
        double score = 0;
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
        return score * 100;
    }

/*
    private double initialMoveScoreFunction(Board board, Move randomMove) {
        double score = 0;
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

 */

    private void backPropagation(Node nodeToExplore, double score) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            tempNode.getState().addScore(score);
            tempNode = tempNode.getParent();
        }
    }
}
