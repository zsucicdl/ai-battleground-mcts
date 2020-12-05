package mcts.montecarlo;

import mcts.game.*;

import java.util.HashMap;
import java.util.List;

public class MonteCarloTreeSearch {
    private static final int SIMULATION_DEPTH = 5;

    public MonteCarloTreeSearch() {
    }

    int getPlayerIndex(int iteration){
        int myPlayerIndex;
        if(iteration == 0 || iteration == 3){
            myPlayerIndex = 0;
        } else if (iteration == 1 || iteration == 5){
            myPlayerIndex = 1;
        } else {
            myPlayerIndex = iteration % 2;
        }
        return myPlayerIndex;
    }

    public Move findNextMove(Board board, int iteration) {
        long start = System.currentTimeMillis();
        long end = start + 4000; // TODO set time limit
        if(iteration < 7) {
            end = start + 2000;
        }

        List<Move> logicalMoves = board.getLogicalMoves();
        List<Move> possibleMoves;
        if(logicalMoves.size() > 0){
            possibleMoves = logicalMoves;
        } else{
            possibleMoves = board.getLegalMoves(iteration);
        }
        int maxScore = Integer.MIN_VALUE;
        int maxIndex = 0;
        while(System.currentTimeMillis() < end) {
            int[] scores = new int[possibleMoves.size()];
            Board[] boards = new Board[possibleMoves.size()];
            for(int i = 0; i < possibleMoves.size(); i++){
                int counter = iteration;
                int myPlayerIndex = getPlayerIndex(counter);
                boards[i] = board.copy();
                if(myPlayerIndex == board.getCurrentPlayerIndex(counter)){
                    scores[i] += scoreFunction(boards[i], possibleMoves.get(i)) / (counter + 1);
                }
                boards[i].playMove(possibleMoves.get(i));
                counter++;
                // now simulate
                for(int depth = 0; depth < SIMULATION_DEPTH; depth++){
                    Move randomMove = boards[i].getRandomMove();
                    if(myPlayerIndex == board.getCurrentPlayerIndex(counter)){
                        scores[i] += scoreFunction(boards[i], randomMove) / (counter + 1);
                    }
                    boards[i].playMove(randomMove);
                    counter++;
                }
                if(scores[i] > maxIndex){
                    maxScore = scores[i];
                    maxIndex = i;
                }
            }
        }
        /*
        int maxScore = Integer.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i <scores.length; i++){
            System.out.println("possible moves: " + scores.length);
            if(scores.length < 5){
                for(int score : scores){
                    System.out.println("scores: " + score);
                }
            }
            if(scores[i] > maxScore){
                maxScore = scores[i];
                maxIndex = i;
            }
        }*/
        System.out.println("Possible Moves");
        possibleMoves.forEach(p -> System.out.println(p.toString()));
        return possibleMoves.get(maxIndex);
    }


    private int scoreFunction(Board board, Move randomMove) {
        int score = 0;
        if(randomMove.getType() == MoveType.INITIAL){
            score += initialMoveScoreFunction(board, randomMove);
        } else if(randomMove.getType() == MoveType.BUILD_TOWN){
            score += 10;
        } else if(randomMove.getType() == MoveType.UPGRADE_TOWN){
            score += 8;
        } else if(randomMove.getType() == MoveType.BUILD_ROAD){
            score += 3;
        }else if(randomMove.getType() == MoveType.MOVE){
            score += 1;
        }
        return score;
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
                        score += f.getWeight() * 7;
                        break;
                    case CLAY:
                        score += f.getWeight() * 6;
                        break;
                    case WHEAT:
                        score += f.getWeight() * 4;
                        break;
                    case SHEEP:
                        score += f.getWeight() * 4;
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
                score = -100000;
            }
        }
        return score *= 10;
    }
}