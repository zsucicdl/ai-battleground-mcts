package mcts.api;

import mcts.game.*;
import mcts.montecarlo.MonteCarloTreeSearch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Main {

    private static final String GAME_ID = "1";
    private static final String URL = "http://localhost:9080/";

    private static final MonteCarloTreeSearch MCTS = new MonteCarloTreeSearch();

    public static void main(String[] args) throws JSONException {
        initPlayer(1);
        initPlayer(2);

    }

    private static void initPlayer(int playerId) throws JSONException {
        String stringJson = HttpHelper.GET(URL + "game/play?playerID=" + playerId + "&gameID=" + GAME_ID);
        JSONObject data = new JSONObject(stringJson);

        int iteration = 0;
        String enemyAction;
        boolean amIFirst = true;
        Board board = null;
        while (data.getBoolean("success")) {
            if(iteration == 0){
                JSONObject result = data.getJSONObject("result");
                JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");
                JSONObject map = result.getJSONObject("map");
                JSONArray mapTiles = map.getJSONArray("tiles");
                JSONArray indexMap = result.getJSONArray("indexMap");

                enemyAction = result.getString("action");
                amIFirst = enemyAction.equals("null");
                board = initGameState(intersectionCoordinates, mapTiles, indexMap, amIFirst);
            } else{
                enemyAction = data.getString("result");
            }

            if(amIFirst && iteration == 0){
                // MOJA POČETNA INICIJALIZACIJA
                stringJson = doMyTurn(board, playerId);
                iteration++;
            } else if(amIFirst && iteration == 1){
                // PROTIVNIČKE DVIJE INICIJALIZACIJE
                String[] words = enemyAction.split(" ");
                String move1 = words[0] + " " + words[1] + " " + words[2];
                String move2 = words[3] + " " + words[4] + " " + words[5];
                board.playMove(Move.fromString(move1));
                iteration++;
                board.playMove(Move.fromString(move2));
                iteration++;
                // MOJA ZADNJA INICIJALIZACIJA I PRVI POTEZ
                doMyTurn(board, playerId);
                iteration++;
                stringJson = doMyTurn(board, playerId);
                iteration++;
            } else if(!amIFirst && iteration == 0){
                board.playMove(Move.fromString(enemyAction));
                iteration++;
                doMyTurn(board, playerId);
                iteration++;
                stringJson = doMyTurn(board, playerId);
                iteration++;
            }else if(!amIFirst && iteration == 3){
                // PROTIVNIČKA ZADNJA INICIJALIZACIJA I PRVI POTEZ
                String[] words = enemyAction.split(" ");
                String move1 = words[0] + " " + words[1] + " " + words[2];
                String move2 = words[3] + " " + words[4] + " " + words[5];
                board.playMove(Move.fromString(move1));
                board.playMove(Move.fromString(move2));
                iteration += 2;

                //MOJ PRVI POTEZ
                stringJson = doMyTurn(board, playerId);
                iteration++;
            } else {
                board.playMove(Move.fromString(enemyAction));
                iteration++;
                stringJson = doMyTurn(board, playerId);
                iteration++;
            }
            data = new JSONObject(stringJson);
        }
    }

    private static String doMyTurn(Board board, int playerId){
        Move move = MCTS.findNextMove(board);
        board.playMove(move);
        String moveString = move.toString().replaceAll(" ", "%20");
        return HttpHelper.GET(URL + "doAction?playerID=" + playerId + "&gameID=" + GAME_ID + "&action=" + moveString);
    }
    
    public static Board initGameState(JSONArray intersectionCoordinates, JSONArray mapTiles, JSONArray indexMap, boolean amIFirst) throws JSONException {

        Map<ValuesXY, Field> fields = new HashMap<>();
        for (int i = 0; i < mapTiles.length(); i++) {
            JSONArray row = (JSONArray) mapTiles.get(i);
            for (int j = 0; j < row.length(); j++) {
                try {
                    JSONObject fieldJSON = (JSONObject) row.get(j);
                    String resource = fieldJSON.getString("resourceType");
                    int weight = fieldJSON.getInt("resourceWeight");
                    int x = fieldJSON.getInt("x");
                    int y = fieldJSON.getInt("y");

                    Field field = new Field(Resource.valueOf(resource), weight);
                    ValuesXY valuesXY = new ValuesXY(x, y);
                    fields.put(valuesXY, field);
                } catch (ClassCastException ignored) {
                }
            }
        }

        List<List<Field>> intersectionToField = new ArrayList<>();
        for (int i = 0; i < intersectionCoordinates.length(); i++) {
            List<Field> temp = new ArrayList<>();
            JSONArray neighbours = (JSONArray) intersectionCoordinates.get(i);
            for (int j = 0; j < neighbours.length(); j++) {
                JSONObject valuesXY = (JSONObject) neighbours.get(j);
                int x = valuesXY.getInt("x");
                int y = valuesXY.getInt("y");
                Field field = fields.get(new ValuesXY(x, y));
                temp.add(field);
            }
            intersectionToField.add(temp);
        }

        List<List<Integer>> intersectionToIntersection = new ArrayList<>();
        for (int i = 0; i < indexMap.length(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            JSONArray row = (JSONArray) indexMap.get(i);
            for (int j = 0; j < row.length(); j++) {
                int intersection = row.getInt(j);
                temp.add(intersection);
            }
            intersectionToIntersection.add(temp);
        }
        // PREDAJ PODATKE
        return Board.initBoard(intersectionToIntersection, intersectionToField, amIFirst);
    }
}
