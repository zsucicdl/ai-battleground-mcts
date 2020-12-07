package mcts.api;

import mcts.game.Board;
import mcts.game.Move;
import mcts.game.MoveType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new FileReader("karta.json"));
        String content = reader.readLine();
        JSONObject data = new JSONObject(content);
        JSONObject result = data.getJSONObject("result");
        JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");
        JSONObject map = result.getJSONObject("map");
        JSONArray mapTiles = map.getJSONArray("tiles");
        JSONArray indexMap = result.getJSONArray("indexMap");

        Board board1 = Game.initGameState(intersectionCoordinates, mapTiles, indexMap, true);
        Board board2 = board1.copy();

        // INITIAL
        board1.playMove(new Move(MoveType.INITIAL, 10, 11));
        board1.playMove(new Move(MoveType.INITIAL, 14, 15));
        board1.playMove(new Move(MoveType.INITIAL, 35, 36));
        board1.playMove(new Move(MoveType.INITIAL, 55, 56));

        // INITIAL
        board2.playMove(new Move(MoveType.INITIAL, 11, 12));
        board2.playMove(new Move(MoveType.INITIAL, 15, 16));
        board2.playMove(new Move(MoveType.INITIAL, 36, 37));
        board2.playMove(new Move(MoveType.INITIAL, 56, 57));

        System.out.println("finito");
    }
}
