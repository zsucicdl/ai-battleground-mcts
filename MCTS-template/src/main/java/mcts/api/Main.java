package mcts.api;

import mcts.game.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

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
            Scanner sc = new Scanner(System.in);
            String pid = "1";//sc.next();
            JSONObject data = new JSONObject(HttpHelper.GET(host + "train/play?playerID=" + pid + "&gameID=1"));
            int iteration = 0;
            int first_player = 0;
            Board board = null;

            while (data.getBoolean("success")) {
                JSONObject result = data.getJSONObject("result");
                // Lista sa poljima koji okruzuju intersectione duljine 96, oblika [[{"x":0,"y":0}, {"x":0,"y":1}, {"x":1,"y":1}], ...]
                JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");

                JSONObject map = result.getJSONObject("map");
                int mapWidth = map.getInt("width");
                int mapHeight = map.getInt("height");
                // Lista sa podacima o poljima (resourceType, resourceWeight, x, y) u arrayu 9x9
                JSONArray mapTiles = map.getJSONArray("tiles");

                // Lista sa susjedima intersectiona duljine 96, oblika [[1, 10], [0, 2], [1, 3, 12], ...]
                JSONArray indexMap = result.getJSONArray("indexMap");

                // Action
                String action = result.getString("action");
                String enemyAction = "";

                // Player ID
                int playerId = data.getInt("playerID");
                
                if (iteration == 0) {
                    if (action.equals("null")) {
                        first_player = playerId;
                    } else {
                        if (playerId == 1) {
                            first_player = 2;
                        } else {
                            first_player = 1;
                        }
                    }
                    board = initGameState(intersectionCoordinates, mapTiles, indexMap);
                }
                boolean isFirst = (first_player == playerId);

                if (!action.equals("null")) {
                    enemyAction = new JSONObject(action).getString("result");
                    String[] words = enemyAction.split(" ");
                    if (words.length == 6) {
                        String move1 = words[0] + " " +words[1] + " " + words[2];
                        String move2 = words[3] + " " +words[4] + " " + words[5];
                        board.playMove(Move.fromString(move1));
                        board.playMove(Move.fromString(move2));
                        iteration++;
                    } else {
                        Move move = Move.fromString(enemyAction);
                        board.playMove(move);
                    }
                    iteration++;
                }
                String myMove = "";
                // DOHVATI MOJ POTEZ
                Move myRandomMove = board.getRandomMove();
                board.playMove(myRandomMove);
                myMove += myRandomMove.toString().replace(" ", "%20");
                if (iteration == 3 && isFirst) {
                    myRandomMove = board.getRandomMove();
                    board.playMove(myRandomMove);
                    myMove += "%20" + myRandomMove.toString().replace(" ", "%20");
                }
                // ODIGRAJ POTEZ I DOHVATI POTEZ PROTIVNIKA
                data = new JSONObject(HttpHelper.GET(host +
                        "train/doAction?playerID=" + pid + "&gameID=1&action=" + myMove));
                iteration++;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
