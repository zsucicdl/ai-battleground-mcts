package mcts.api;

import mcts.game.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainBotRandom {

    private static String playerId = "1";
    private static String gameId = "1";
    
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
        Board board = Board.initBoard(intersectionToIntersection, intersectionToField, amIFirst);
        return board;
    }

    public static void main(String[] args) {

        String host = "http://localhost:9080/";
        try {
            // JOIN
            Scanner sc = new Scanner(System.in);
            JSONObject data = new JSONObject(HttpHelper.GET(host + "game/play?playerID=" + playerId + "&gameID=1"));
            int iteration = 0;
            boolean amIFirst = true;
            Board board = null;

            while (data.getBoolean("success")) {
                String enemyAction = "";
                if(iteration == 0){
                    JSONObject result = data.getJSONObject("result");
                    // Action
                    String action = result.getString("action");
                    enemyAction = action;

                    // Player ID
                    int playerId = data.getInt("playerID");
                    // Lista sa poljima koji okruzuju intersectione duljine 96, oblika [[{"x":0,"y":0}, {"x":0,"y":1}, {"x":1,"y":1}], ...]
                    JSONArray intersectionCoordinates = result.getJSONArray("intersectionCoordinates");

                    JSONObject map = result.getJSONObject("map");
                    int mapWidth = map.getInt("width");
                    int mapHeight = map.getInt("height");
                    // Lista sa podacima o poljima (resourceType, resourceWeight, x, y) u arrayu 9x9
                    JSONArray mapTiles = map.getJSONArray("tiles");

                    // Lista sa susjedima intersectiona duljine 96, oblika [[1, 10], [0, 2], [1, 3, 12], ...]
                    JSONArray indexMap = result.getJSONArray("indexMap");

                    if (action.equals("null")) {
                        amIFirst = true;
                    } else {
                        amIFirst = false;
                    }
                    board = initGameState(intersectionCoordinates, mapTiles, indexMap, amIFirst);
                } else{
                    enemyAction = data.getString("result");
                }
                String myMove = "";
                if(amIFirst && iteration == 0){
                    Move myRandomMove = board.getRandomMove();
                    board.playMove(myRandomMove);
                    myMove += myRandomMove.toString();
                    iteration++;
                } else if(amIFirst && iteration == 1){
                    String[] words = enemyAction.split(" ");
                    String move1 = words[0] + " " + words[1] + " " + words[2];
                    String move2 = words[3] + " " + words[4] + " " + words[5];
                    board.playMove(Move.fromString(move1));
                    board.playMove(Move.fromString(move2));
                    iteration += 2;

                    Move move = board.getRandomMove();
                    board.playMove(move);
                    myMove += move.toString();
                    myMove = myMove.replaceAll(" ", "%20");
                    HttpHelper.GET(host + "doAction?playerID=" + playerId + "&gameID=1&action=" + myMove);

                    myMove = "";
                    move = board.getRandomMove();
                    board.playMove(move);
                    myMove += move.toString();
                    iteration += 2;
                } else if(!amIFirst && iteration == 0){
                    board.playMove(Move.fromString(enemyAction));
                    iteration++;

                    Move move = board.getRandomMove();
                    board.playMove(move);
                    myMove += move.toString();

                    myMove = myMove.replaceAll(" ", "%20");
                    HttpHelper.GET(host + "doAction?playerID=" + playerId + "&gameID=1&action=" + myMove);

                    myMove = "";
                    move = board.getRandomMove();
                    board.playMove(move);
                    myMove += move.toString();
                    iteration += 2;
                }else if(!amIFirst && iteration == 3){
                    String[] words = enemyAction.split(" ");
                    String move1 = words[0] + " " + words[1] + " " + words[2];
                    String move2 = "";
                    for (String s : words) {
                        move2 += s + " ";
                    }
                    move2 = move2.strip();
                    board.playMove(Move.fromString(move1));
                    board.playMove(Move.fromString(move2));
                    iteration += 2;

                    Move move = board.getRandomMove();
                    board.playMove(move);
                    myMove += move.toString();
                } else {

                    board.playMove(Move.fromString(enemyAction));
                    iteration++;

                    List<Move> moves = board.getLegalMoves();
                    System.out.println("Possible moves: " + moves.size());
                    if(moves.size() <= 5){
                        moves.forEach(m -> System.out.println(m.toString()));
                    }
                    Move move = board.getRandomMove();
                    System.out.println("MyMove: " + move.toString());
                    board.playMove(move);
                    myMove += move.toString();
                    iteration++;
                }
                myMove = myMove.replaceAll(" ", "%20");
                // ODIGRAJ POTEZ I DOHVATI POTEZ PROTIVNIKA
                data = new JSONObject(HttpHelper.GET(host + "doAction?playerID=" + playerId + "&gameID=1&action=" + myMove));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
