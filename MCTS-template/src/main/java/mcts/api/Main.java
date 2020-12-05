package mcts.api;

import mcts.game.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Main {

    private static String playerId = "1";
    private static String gameId = "1";
    
    public static Board initGameState(JSONArray intersectionCoordinates, JSONArray mapTiles, JSONArray indexMap) throws JSONException {

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
                } catch (ClassCastException e) {
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
        Board board = Board.initBoard(intersectionToIntersection, intersectionToField);
        return board;
    }

    public static void main(String[] args) {
        String host = "http://localhost:9080/";
        try {
            // JOIN
            JSONObject res = new JSONObject(HttpHelper.GET(host + "train/play?playerID=1&gameID=1"));
            int iteration = 0;
            ArrayList<String> myActions = new ArrayList<>();
            myActions.add("initial%204%205");
            myActions.add("initial%2050%2050");
            myActions.add("initial%2050%2050");
            myActions.add("initial%2050%2050");
            Board board = null;

            while (res.getBoolean("success")) {
                JSONObject result = res.getJSONObject("result");

                // Lista sa poljima koji okruzuju intersectione duljine 96, oblika [[{"x":0,"y":0}, {"x":0,"y":1}, {"x":1,"y":1}], ...]
                JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");

                JSONObject map = result.getJSONObject("map");
                int mapWidth = map.getInt("width");
                int mapHeight = map.getInt("height");
                // Lista sa podacima o poljima (resourceType, resourceWeight, x, y) u arrayu 9x9
                JSONArray mapTiles = map.getJSONArray("tiles");

                // Lista sa susjedima intersectiona duljine 96, oblika [[1, 10], [0, 2], [1, 3, 12], ...]
                JSONArray indexMap = result.getJSONArray("indexMap");

                if (iteration == 0) {
                    board = initGameState(intersectionCoordinates, mapTiles, indexMap);
                }

                // Action
                String action = result.getString("action");

                // Player ID
                int playerId = res.getInt("playerID");
                // DOHVATI MOJ POTEZ
                String myRandomMove = board.getRandomMove().toString().replace(" ", "%20");
                // ODIGRAJ POTEZ I DOHVATI POTEZ PROTIVNIKA
                JSONObject enemyJSON = new JSONObject(HttpHelper.GET(host +
                        "train/doAction?playerID=1&gameID=1&action=" + myRandomMove));
                iteration++;

                String enemyAction = enemyJSON.getString("result");

                // PROVEDI POTEZ PROTIVNIKA
                Move move = Move.fromString(enemyAction);
                board.playMove(move);
                iteration++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
