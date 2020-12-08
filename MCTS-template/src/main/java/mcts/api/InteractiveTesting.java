package mcts.api;

import mcts.game.Board;
import mcts.game.Move;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveTesting {
    private static final int GAME_ID = 1;
    private static final String URL = "http://localhost:9080/";

    public static void main(String[] args) throws JSONException {
        int playerId = 1;
        Scanner sc = new Scanner(System.in);
        String content = HttpHelper.GET(URL + "train/play?playerID=" + playerId + "&gameID=" + GAME_ID);

        JSONObject data = new JSONObject(content);
        JSONObject result = data.getJSONObject("result");
        JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");
        JSONObject map = result.getJSONObject("map");
        JSONArray mapTiles = map.getJSONArray("tiles");
        JSONArray indexMap = result.getJSONArray("indexMap");
        Board board1 = Game.initGameState(intersectionCoordinates, mapTiles, indexMap, true);

        String res, enemyAction, enemyAction1;
        List<Move> myMoves = new ArrayList<>();
        List<Move> opponentMoves = new ArrayList<>();
        while(true){
            String line = sc.nextLine().strip();
            Move move = Move.fromString(line);
            myMoves.add(move);
            List<Move> possibleMoves = board1.getLegalMoves();
            board1.playMove(move);
            String moveString = move.toString().replaceAll(" ", "%20");

            res = HttpHelper.GET(URL + "train/doAction?playerID=" + playerId + "&gameID=" + GAME_ID + "&action=" + moveString);
            enemyAction = new JSONObject(res).getString("result");
            if (board1.getTurns() == 4){
                line = sc.nextLine().strip();
                move = Move.fromString(line);
                myMoves.add(move);
                possibleMoves = board1.getLegalMoves();
                board1.playMove(move);
                moveString = move.toString().replaceAll(" ", "%20");

                res = HttpHelper.GET(URL + "train/doAction?playerID=" + playerId + "&gameID=" + GAME_ID + "&action=" + moveString);
                enemyAction = new JSONObject(res).getString("result");
            }
            if(enemyAction.split(" ").length == 6){
                String[] words = enemyAction.split(" ");
                enemyAction1 = words[0] + " " + words[1] + " " + words[2];
                board1.playMove(Move.fromString(enemyAction1));
                opponentMoves.add(Move.fromString(enemyAction1));
                enemyAction = words[3] + " " + words[4] + " " + words[5];
            }
            Move enemyMove = Move.fromString(enemyAction);
            board1.playMove(enemyMove);
            opponentMoves.add(enemyMove);

        }
    }
}
//initial 30 29
//initial 70 71
