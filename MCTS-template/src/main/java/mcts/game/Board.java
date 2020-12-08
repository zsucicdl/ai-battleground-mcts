package mcts.game;

import java.util.*;

public class Board {
    private static final Random RANDOM = new Random();
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

    public int getTurns() {
        return turns;
    }

    public Builder[] getPlayers() {
        return players;
    }

    public HashMap<Integer, City>[] getIndexCities() {
        return indexCities;
    }

    public HashMap<Integer, Intersection> getIndexIntersections() {
        return indexIntersections;
    }

    public HashMap<ValuesXY, Integer> getIndexXYRoads() {
        return indexXYRoads;
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

    public Builder getCurrentPlayer(){
        return players[getCurrentPlayerIndex()];
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
                if(intersection.adjacentToCity(indexCities) ||  currentCities.containsKey(intersection.getIndex()) || opponentCities.containsKey(intersection.getIndex())){
                    continue;
                }
                // if town placement will not satisfy 4 basic resources, then it is not legal move
                if(currentCities.size() > 0){
                    HashMap<Resource, Boolean> flags = new HashMap<>();
                    for(City c : currentCities.values()){
                        for(Field f : c.getIntersection().getAdjacentFields()){
                            flags.put(f.getResource(), true);
                        }
                    }
                    for(Field f : intersection.getAdjacentFields()){
                        flags.put(f.getResource(), true);
                    }
                    if(!flags.containsKey(Resource.WOOD) || !flags.containsKey(Resource.SHEEP) || !flags.containsKey(Resource.WHEAT) || !flags.containsKey(Resource.CLAY)){
                        continue;
                    }
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
                if (getRoadStatus(currentPlayer.getCurrentIntersection().getIndex(), intersection.getIndex()) == currentPlayer.getPlayerId()) {
                    moves.add(new Move(MoveType.MOVE, intersection.getIndex()));
                } /*else if (currentPlayer.getAvailableResources().get(Resource.SHEEP) >= 50 && currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 50) {
                    moves.add(new Move(MoveType.MOVE, intersection.getIndex()));
                } */

                // BUILD ROAD
                if (getRoadStatus(currentPlayer.getCurrentIntersection().getIndex(), intersection.getIndex()) != 0) {
                    continue;
                }
                boolean isConnected = false;
                if (currentPlayer.getCurrentIntersection().isConnected(currentPlayer.getPlayerId(), this) ||
                        intersection.isConnected(currentPlayer.getPlayerId(), this) ||
                        currentCities.get(currentPlayer.getCurrentIntersection().getIndex()) != null ||
                        currentCities.get(intersection.getIndex()) != null) {
                    isConnected = true;
                }
                if (!isConnected || opponentCities.containsKey(currentPlayer.getCurrentIntersection().getIndex()) ||
                        opponentCities.containsKey(intersection.getIndex()) ||
                        currentPlayer.getCurrentIntersection().numberOfRoads(opponentPlayer.getPlayerId(), this) == 2 ||
                        intersection.numberOfRoads(opponentPlayer.getPlayerId(), this) == 2) {
                    continue;
                }
                if (currentPlayer.getAvailableResources().get(Resource.WOOD) >= 100 && currentPlayer.getAvailableResources().get(Resource.CLAY) >= 100) {
                    moves.add(new Move(MoveType.BUILD_ROAD, intersection.getIndex()));
                }
            }

            // BUILD TOWN
            if(!currentCities.containsKey(currentPlayer.getCurrentIntersection().getIndex()) &&
                    !opponentCities.containsKey(currentPlayer.getCurrentIntersection().getIndex()) &&
                    !currentPlayer.getCurrentIntersection().adjacentToCity(indexCities) &&
                    currentPlayer.getCurrentIntersection().isConnected(currentPlayer.getPlayerId(), this) &&
                    currentPlayer.getCurrentIntersection().numberOfRoads(opponentPlayer.getPlayerId(), this) != 2 &&
                    currentPlayer.getAvailableResources().get(Resource.SHEEP) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.WOOD) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 100 &&
                    currentPlayer.getAvailableResources().get(Resource.CLAY) >= 100){
                moves.add(new Move(MoveType.BUILD_TOWN));
            }

            // UPGRADE TOWN
            if(currentPlayer.getAvailableResources().get(Resource.WHEAT) >= 200 && currentPlayer.getAvailableResources().get(Resource.IRON) >= 300){
                for(Integer index : currentCities.keySet()){
                    if(currentCities.get(index).getLevel() == 1) {
                        moves.add(new Move(MoveType.UPGRADE_TOWN, index));
                    }
                }
            }

            // EMPTY
            if(turns >= 4){
                moves.add(new Move(MoveType.EMPTY));
            }
        }
        return moves;
    }

    public void playMove(Move move){
        int currentPlayerIndex = getCurrentPlayerIndex();
        Builder currentPlayer = players[currentPlayerIndex];
        HashMap<Integer, City> currentCities = indexCities[currentPlayerIndex];

        if(turns >= 4){
            gainResources(0);
            gainResources(1);
        }

        if(move.getType() == MoveType.INITIAL){
            if(turns == 0 || turns == 1){
                System.out.println("Player " + currentPlayer.getPlayerId() + " is placed on: " + move.getIndex1());
                System.out.println("The intersection: " + indexIntersections.get(move.getIndex1()).getIndex());
                currentPlayer.setCurrentIntersection(indexIntersections.get(move.getIndex1()));
            }
            currentCities.put(move.getIndex1(), new City(indexIntersections.get(move.getIndex1())));
            buildRoad(move.getIndex1(), move.getIndex2(), currentPlayer.getPlayerId());
        } else if(move.getType().equals(MoveType.MOVE)){
            if(getRoadStatus(currentPlayer.getCurrentIntersection().getIndex(), move.getIndex1()) != currentPlayer.getPlayerId()){
                currentPlayer.getAvailableResources().compute(Resource.SHEEP, (key, value) -> value - 50);
                currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 50);
            }
            currentPlayer.setCurrentIntersection(indexIntersections.get(move.getIndex1()));
        } else if(move.getType().equals(MoveType.BUILD_ROAD)){
            buildRoad(currentPlayer.getCurrentIntersection().getIndex(), move.getIndex1(), currentPlayer.getPlayerId());
            currentPlayer.getAvailableResources().compute(Resource.WOOD, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.CLAY, (key, value) -> value - 100);
        } else if(move.getType().equals(MoveType.BUILD_TOWN)){
            currentCities.put(currentPlayer.getCurrentIntersection().getIndex(), new City(currentPlayer.getCurrentIntersection()));
            currentPlayer.getAvailableResources().compute(Resource.SHEEP, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.WOOD, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 100);
            currentPlayer.getAvailableResources().compute(Resource.CLAY, (key, value) -> value - 100);
            currentPlayer.gainPoint();
        } else if(move.getType().equals(MoveType.UPGRADE_TOWN)){
            currentCities.get(move.getIndex1()).upgrade();
            currentPlayer.getAvailableResources().compute(Resource.WHEAT, (key, value) -> value - 200);
            currentPlayer.getAvailableResources().compute(Resource.IRON, (key, value) -> value - 300);
            currentPlayer.gainPoint();
        } else if(move.getType().equals(MoveType.EMPTY)){
        }
        turns++;
    }

    private void gainResources(int playerIndex) {
        Builder currentPlayer = players[playerIndex];
        HashMap<Integer, City> currentCities = indexCities[playerIndex];

        for(City city : currentCities.values()){
            for(Field field : city.getIntersection().getAdjacentFields()){
                if(field.getResource() != Resource.WATER && field.getResource() != Resource.DUST){
                    currentPlayer.getAvailableResources().compute(field.getResource(), (key, value) -> value + field.getWeight() * city.getLevel());
                }
            }
        }
    }

    public Move getRandomMove(){
        List<Move> possibleMoves = getLegalMoves();
        if(possibleMoves.size() == 0){
            return null;
        }
        int index = RANDOM.nextInt(possibleMoves.size());
        return possibleMoves.get(index);
    }

    public boolean isRunning(){
        return players[0].getPoints() < 16 && players[1].getPoints() < 16;
    }

    public int getRoadStatus(int index1, int index2){
        if(!indexXYRoads.containsKey(new ValuesXY(index1, index2))){
            return 0;
        }
        return indexXYRoads.get(new ValuesXY(index1, index2));
    }

    public void buildRoad(int index1, int index2, int playerId){
        indexXYRoads.put(new ValuesXY(index1, index2), playerId);
        indexXYRoads.put(new ValuesXY(index2, index1), playerId);
    }

    public Move getBestInitialMove() {
        HashMap<Resource, Integer> resources = new HashMap<>();
        for(City c : indexCities[getCurrentPlayerIndex()].values()){
            for(Field f : c.getIntersection().getAdjacentFields()){
                if(resources.containsKey(f.getResource())){
                   resources.compute(f.getResource(), (key, value) -> value + f.getWeight());
                } else {
                    resources.put(f.getResource(), f.getWeight());
                }
            }
        }

        Move bestMove = null;
        int maxScore = Integer.MIN_VALUE;
        for(Move m : getLegalMoves()){
            int score = 0;
            for(Field f : indexIntersections.get(m.getIndex1()).getAdjacentFields()){
                double tempScore = 0;
                switch (f.getResource()){
                    case WOOD:
                    case CLAY:
                        tempScore += f.getWeight();
                        if(resources.containsKey(f.getResource())){
                            tempScore *= (30 - resources.get(f.getResource()));
                        }else{
                            tempScore *= 30;
                        }
                        break;
                    case SHEEP:
                    case WHEAT:
                        tempScore += f.getWeight();
                        if(resources.containsKey(f.getResource())){
                            tempScore *= (30 - resources.get(f.getResource()));
                        }else{
                            tempScore *= 30;
                        }
                        break;
                    case IRON:
                        tempScore += f.getWeight();
                        if(resources.containsKey(f.getResource())){
                            tempScore *= (30 - resources.get(f.getResource()));
                        }else{
                            tempScore *= 30;
                        }
                }

                score += tempScore;
            }
            if(score > maxScore){
                maxScore = score;
                bestMove = m;
            }
        }
        return bestMove;
    }
}
