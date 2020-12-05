package mcts.montecarlo;

import mcts.game.Board;
import mcts.game.Move;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Board board;
    private int visitCount;
    private double winScore;

    public State() {
    }

    public State(Board board) {
        this.board = board.copy();
    }

    public State copy(){
        State newState = new State();
        newState.setBoard(this.board.copy());
        newState.setVisitCount(this.visitCount);
        newState.setWinScore(this.winScore);
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

    double getWinScore() {
        return winScore;
    }

    void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();
        List<Move> legalMoves = this.board.getLegalMoves();
        legalMoves.forEach(move -> {
            State newState = new State(this.board);
            newState.getBoard().playMove(move);
            possibleStates.add(newState);
        });
        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() {
        Move randomMove = this.board.getRandomMove();
        this.board.playMove(randomMove);
    }
}
