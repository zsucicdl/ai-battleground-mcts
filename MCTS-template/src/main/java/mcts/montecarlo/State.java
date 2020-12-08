package mcts.montecarlo;

import mcts.game.Board;
import mcts.game.Move;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Move initialMove;

    private Board board;
    private int visitCount;
    private double winScore;

    public State() {
    }

    public State(Board board, Move initialMove) {
        this.board = board;
        this.initialMove = initialMove;
    }

    public Move getInitialMove() {
        return initialMove;
    }

    public void setInitialMove(Move initialMove) {
        this.initialMove = initialMove;
    }

    public State copy(){
        State newState = new State();
        newState.setBoard(this.board.copy());
        newState.setVisitCount(this.visitCount);
        newState.setWinScore(this.winScore);
        newState.setInitialMove(this.initialMove);
        return newState;
    }


    Board getBoard() {
        return this.board;
    }

    void setBoard(Board board) {
        this.board = board;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public double getWinScore() {
        return winScore;
    }

    void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();
        List<Move> legalMoves = this.board.getLegalMoves();
        for (Move move : legalMoves) {
            State newState = new State(this.board.copy(), move);
            newState.getBoard().playMove(move);
            possibleStates.add(newState);
        }
        return possibleStates;
    }

    public void incrementVisit() {
        this.visitCount++;
    }

    public void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    public void randomPlay() {
        Move randomMove = this.board.getRandomMove();
        this.board.playMove(randomMove);
    }
}
