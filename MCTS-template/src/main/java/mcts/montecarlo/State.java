package mcts.montecarlo;

import mcts.tictactoe.Board;
import mcts.tictactoe.Move;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Board board;
    private int playerNo;
    private int visitCount;
    private double winScore;

    public State() {
        board = new Board();
    }

    public State(Board board) {
        this.board = board.copy();
    }

    public State copy(){
        State newState = new State();
        newState.setBoard(this.board.copy());
        newState.setPlayerNo(this.playerNo);
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

    int getPlayerNo() {
        return playerNo;
    }

    void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }

    int getOpponent() {
        return 3 - playerNo;
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
        List<Move> availablePositions = this.board.getPossibleMoves();
        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(3 - this.playerNo);
            newState.getBoard().performMove(newState.getPlayerNo(), p);
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
        List<Move> possibleMoves = this.board.getPossibleMoves();
        int totalPossibilities = possibleMoves.size();
        int selectRandom = (int) (Math.random() * totalPossibilities);
        this.board.performMove(this.playerNo, possibleMoves.get(selectRandom));
    }

    void togglePlayer() {
        this.playerNo = 3 - this.playerNo;
    }
}
