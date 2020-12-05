package mcts.game;

import java.util.*;

public class Board {
    private int turns;

    private Builder[] players;
    private HashMap<Integer, City>[] indexCities;

    private HashMap<Integer, Intersection> indexIntersections;
    private HashMap<ValuesXY, Integer> indexXYRoads; // 0 - nema ceste; 1 - cesta od igrača 1; 2 - cesta od igrača 2

    public Board(int turns, Builder[] players, HashMap<Integer, City>[] indexCities, HashMap<Integer, Intersection> indexIntersections, HashMap<ValuesXY, Integer> indexXYRoads) {
        this.turns = turns;
        this.players = players;
        this.indexCities = indexCities;
        this.indexIntersections = indexIntersections;
        this.indexXYRoads = indexXYRoads;
    }

    public Board copy(){
        // copy cities
        HashMap<Integer, City>[] newIndexCities = new HashMap[] {new HashMap<>(), new HashMap<>()};
        for(int i = 0; i < 2; i++){
            for(Map.Entry<Integer, City> entry : indexCities[i].entrySet()){
                newIndexCities[i].put(entry.getKey(), entry.getValue().copy());
            }
        }
        // copy roads
        HashMap<ValuesXY, Integer> newIndexXYRoads = new HashMap<>();
        for(Map.Entry<ValuesXY, Integer> entry : indexXYRoads.entrySet()){
            newIndexXYRoads.put(entry.getKey(), entry.getValue());
        }
        //copy players
        Builder[] newPlayers = new Builder[] {players[0].copy(), players[1].copy()};
        return new Board(turns, newPlayers, newIndexCities, indexIntersections, newIndexXYRoads);
    }

    public static Board initBoard(List<List<Integer>> intersectionToIntersection, List<List<Field>> intersectionToField, boolean amIFirst){
        HashMap<Integer, Intersection> indexIntersections = new HashMap<>();
        HashMap<ValuesXY, Integer> intersectionRoads = new HashMap<>();

        int n = intersectionToIntersection.size();
        for(int i = 0; i < n; i++){
            if(!indexIntersections.containsKey(i)){
                indexIntersections.put(i, new Intersection(i));
            }
            Intersection currentIntersection = indexIntersections.get(i);
            for(Integer index : intersectionToIntersection.get(i)){
                if(!indexIntersections.containsKey(index)){
                    indexIntersections.put(index, new Intersection(index));
                }
                currentIntersection.addAdjacentIntersection(indexIntersections.get(index));
            }

            for(Field field : intersectionToField.get(i)){
                currentIntersection.addAdjacentField(field);
            }
        }
        Builder player1 = new Builder(amIFirst ? 1 : 2);
        Builder player2 = new Builder(amIFirst ? 2 : 1);
        Builder[] players = new Builder[] {player1, player2};
        HashMap<Integer, City>[] newIndexCities = new HashMap[] {new HashMap<>(), new HashMap<>()};
        return new Board(0, players, newIndexCities, indexIntersections, intersectionRoads);
    }

    public int getCurrentPlayerIndex(){
        if(turns == 0 || turns == 3){
            return 0;
        } else if(turns == 1 || turns == 2){
            return 1;
        }else {
            if(turns % 2 == 0){
                return 0;
            } else {
                return 1;
            }
        }
    }

    public List<Move> getLegalMoves(){
        Builder currentPlayer = players[getCurrentPlayerIndex()];
        Builder opponentPlayer = players[1 - getCurrentPlayerIndex()];
        HashMap<Integer, City> currentCities = indexCities[getCurrentPlayerIndex()];
        HashMap<Integer, City> opponentCities = indexCities[1 - getCurrentPlayerIndex()];

        List<Move> moves = new ArrayList<>();
        // check initial move
        if(turns < 4) {
            for(Intersection intersection : indexIntersections.values()){
                if(currentCities.containsKey(intersection.getIndex()) || opponentCities.containsKey(intersection.getIndex())){
                    continue;
                }
                for(Intersection otherIntersection : intersection.getAdjacentIntersections()){
                    if(indexXYRoads.containsKey(new ValuesXY(intersection.getIndex(), otherIntersection.getIndex()))){
                        continue;
                    }
                    moves.add(new Move(MoveType.INITIAL, intersection.getIndex(), otherIntersection.getIndex()));
                }
            }
        } else {
            // MOVE & BUILD ROAD
            for(Intersection intersection : currentPlayer.getCurrentIntersection().getAdjacentIntersections()) {
                // MOVE
                if (indexXYRoads.get(new ValuesXY(currentPlayer.getCurrentIntersection().getIndex(), intersection.getIndex())) == currentPlayer.getPlayerId()) {
                    moves.add(new Move(MoveType.MOVE, intersection.getIndex()));
                } else if (currentPlayer.getAvailableResources().get(Resource.SHEEP) >= 50 && currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 50) {
                    moves.add(new Move(MoveType.MOVE, intersection.getIndex()));
                }

                // BUILD ROAD
                int connection = indexXYRoads.get(new ValuesXY(currentPlayer.getCurrentIntersection().getIndex(), intersection.getIndex()));
                if (connection != 0) {
                    continue;
                }
                boolean isConnected = false;
                if (currentPlayer.getCurrentIntersection().isConnected(currentPlayer.getPlayerId(), indexXYRoads) || intersection.isConnected(currentPlayer.getPlayerId(), indexXYRoads)) {
                    isConnected = true;
                }
                if (currentCities.get(currentPlayer.getCurrentIntersection().getIndex()) != null || currentCities.get(intersection.getIndex()) != null) {
                    isConnected = true;
                }
                if (!isConnected || opponentCities.containsKey(currentPlayer.getCurrentIntersection().getIndex()) ||
                        opponentCities.containsKey(intersection.getIndex()) ||
                        currentPlayer.getCurrentIntersection().numberOfRoads(opponentPlayer.getPlayerId(), indexXYRoads) == 2 ||
                        intersection.numberOfRoads(opponentPlayer.getPlayerId(), indexXYRoads) == 2) {
                    continue;
                }
                if (currentPlayer.getAvailableResources().get(Resource.WOOD) >= 100 && currentPlayer.getAvailableResources().get(Resource.CLAY) >= 100) {
                    moves.add(new Move(MoveType.BUILD_ROAD, intersection.getIndex()));
                }
            }

            // BUILD TOWN
            if(currentPlayer.getCurrentIntersection().isConnected(currentPlayer.getPlayerId(), indexXYRoads) &&
                    !currentPlayer.getCurrentIntersection().adjacentToCity(indexCities) &&
                    currentPlayer.getCurrentIntersection().numberOfRoads(opponentPlayer.getPlayerId(), indexXYRoads) != 2 &&
                    currentPlayer.getAvailableResources().get(Resource.SHEEP) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.WOOD) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.CLAY) >= 100){
                moves.add(new Move(MoveType.BUILD_TOWN));
            }

            // UPGRADE TOWN
            if(currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 200 && currentPlayer.getAvailableResources().get(Resource.IRON) >= 300){
                for(Integer index : currentCities.keySet()){
                    moves.add(new Move(MoveType.UPGRADE_TOWN, index));
                }
            }

            // EMPTY
            moves.add(new Move(MoveType.EMPTY));
        }
        return moves;
    }

    public void playMove(Move move){
        int currentPlayerIndex = getCurrentPlayerIndex();
        Builder currentPlayer = players[currentPlayerIndex];
        Builder opponentPlayer = players[1 - currentPlayerIndex];
        HashMap<Integer, City> currentCities = indexCities[currentPlayerIndex];
        HashMap<Integer, City> opponentCities = indexCities[1 - currentPlayerIndex];
        turns++;

        gainResouces(0);
        gainResouces(1);

        if(move.getType().equals(MoveType.INITIAL)){
            if(turns == 0 || turns == 1){
                currentPlayer.setCurrentIntersection(indexIntersections.get(move.getIndex1()));
            }
            indexCities[currentPlayerIndex].put(move.getIndex1(), new City(indexIntersections.get(move.getIndex1())));
            indexXYRoads.put(new ValuesXY(move.getIndex1(), move.getIndex2()), players[currentPlayerIndex].getPlayerId());
        } else if(move.getType().equals(MoveType.MOVE)){
            if(indexXYRoads.get(new ValuesXY(currentPlayer.getCurrentIntersection().getIndex(), move.getIndex1())) != currentPlayer.getPlayerId()){
                currentPlayer.getAvailableResources().compute(Resource.SHEEP, (key, value) -> value - 50);
                currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 50);
            }
            currentPlayer.setCurrentIntersection(indexIntersections.get(move.getIndex1()));
        } else if(move.getType().equals(MoveType.BUILD_ROAD)){
            indexXYRoads.put(new ValuesXY(currentPlayer.getCurrentIntersection().getIndex(), move.getIndex1()), currentPlayer.getPlayerId());
            currentPlayer.getAvailableResources().compute(Resource.WOOD, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.CLAY, (key, value) -> value - 100);
        } else if(move.getType().equals(MoveType.BUILD_TOWN)){
            currentCities.put(currentPlayer.getCurrentIntersection().getIndex(), new City(currentPlayer.getCurrentIntersection()));
            currentPlayer.getAvailableResources().compute(Resource.SHEEP, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.WOOD, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.CLAY, (key, value) -> value - 100);
        } else if(move.getType().equals(MoveType.UPGRADE_TOWN)){
            currentCities.get(move.getIndex1()).upgrade();
            currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 200);
            currentPlayer.getAvailableResources().compute(Resource.IRON, (key, value) -> value - 300);
        } else if(move.getType().equals(MoveType.EMPTY)){
        }
    }

    private void gainResouces(int playerIndex) {
        Builder currentPlayer = players[playerIndex];
        HashMap<Integer, City> currentCities = indexCities[playerIndex];

        for(City city : currentCities.values()){
            for(Field field : city.getIntersection().getAdjacentFields()){
                currentPlayer.getAvailableResources().compute(field.getResource(), (key, value) -> value + field.getWeight() * city.getLevel());
            }
        }
    }

    public Move getRandomMove(){
        List<Move> possibleMoves = getLegalMoves();
        int index = new Random().nextInt(possibleMoves.size());
        return possibleMoves.get(index);
    }

    public boolean isRunning(){
        if(players[0].getPoints() < 16 && players[1].getPoints() < 16){
            return true;
        }
        return false;
    }
}
