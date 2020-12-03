package mcts.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    public static final int DEFAULT_BOARD_SIZE = 3;
    public static final int IN_PROGRESS = 0;

    int[][] boardValues;
    int totalMoves;

    public Board() {
        boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE];
        totalMoves = 0;
    }

    public Board copy(){
        Board newBoard = new Board();
        int boardLength = this.boardValues.length;
        int[][] boardValues = new int[boardLength][boardLength];
        for (int i = 0; i < this.boardValues.length; i++) {
            boardValues[i] = Arrays.copyOf(this.boardValues[i], this.boardValues[i].length);
        }
        return newBoard;
    }

    public void performMove(int player, Move m) {
        this.totalMoves++;
        // TODO perform move
    }

    public int[][] getBoardValues() {
        return boardValues;
    }

    public void setBoardValues(int[][] boardValues) {
        this.boardValues = boardValues;
    }

    public int checkStatus() {
        // TODO check game status
        return 0;
    }

    public List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<Move>();
        // TODO find possible moves
        return possibleMoves;
    }

    public void printBoard() {
        int size = this.boardValues.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(boardValues[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
