package mcts.game;

import java.util.HashMap;
import java.util.List;

public class Board {
    private Builder player1;
    private Builder player2;

    private City[] cities1;
    private City[] cities2;

    private HashMap<Integer, Intersection> indexIntersections;
    private HashMap<ValuesXY, Boolean> intersectionRoads;

    /*
    *  private Builder[] players;
    * private City[][] cities;
    *
    *  */

    public Board(Builder player1, Builder player2, City[] cities1, City[] cities2,
                 HashMap<Integer, Intersection> indexIntersections,
                 HashMap<ValuesXY, Boolean> intersectionRoads) {
        this.player1 = player1;
        this.player2 = player2;
        this.cities1 = cities1;
        this.cities2 = cities2;
        this.indexIntersections = indexIntersections;
        this.intersectionRoads = intersectionRoads;
    }

    public Builder getPlayer1() {
        return player1;
    }

    public void setPlayer1(Builder player1) {
        this.player1 = player1;
    }

    public Builder getPlayer2() {
        return player2;
    }

    public void setPlayer2(Builder player2) {
        this.player2 = player2;
    }

    public City[] getCities1() {
        return cities1;
    }

    public void setCities1(City[] cities1) {
        this.cities1 = cities1;
    }

    public City[] getCities2() {
        return cities2;
    }

    public void setCities2(City[] cities2) {
        this.cities2 = cities2;
    }

    public HashMap<Integer, Intersection> getIndexIntersections() {
        return indexIntersections;
    }

    public void setIndexIntersections(HashMap<Integer, Intersection> indexIntersections) {
        this.indexIntersections = indexIntersections;
    }

    public HashMap<ValuesXY, Boolean> getIntersectionRoads() {
        return intersectionRoads;
    }

    public void setIntersectionRoads(HashMap<ValuesXY, Boolean> intersectionRoads) {
        this.intersectionRoads = intersectionRoads;
    }

    public Board copy(){
        City[] newCities1 = new City[] {this.cities1[0].copy(), this.cities1[1].copy()};
        City[] newCities2 = new City[] {this.cities2[0].copy(), this.cities2[1].copy()};

        HashMap<ValuesXY, Boolean> newIntersectionRoads = new HashMap<>(this.intersectionRoads);
        return new Board(player1.copy(), player2.copy(), newCities1, newCities2, this.indexIntersections, newIntersectionRoads);
    }

    public static Board initBoard(List<List<Integer>> intersectionToIntersection, List<List<Field>> intersectionToField){
        HashMap<Integer, Intersection> indexIntersections = new HashMap<>();
        HashMap<ValuesXY, Boolean> intersectionRoads = new HashMap<>();

        int n = intersectionToIntersection.size();

        for(int i = 0; i < n; i++){
            if(!indexIntersections.containsKey(i)){
                indexIntersections.put(i, new Intersection());
            }
            Intersection currentIntersection = indexIntersections.get(i);
            for(Integer index : intersectionToIntersection.get(i)){
                if(!indexIntersections.containsKey(index)){
                    indexIntersections.put(index, new Intersection());
                }
                currentIntersection.addAdjacentIntersection(indexIntersections.get(index));
            }

            for(Field field : intersectionToField.get(i)){
                currentIntersection.addAdjacentField(field);
            }
        }
        return new Board(new Builder(1), new Builder(2), new City[2], new City[2], indexIntersections, intersectionRoads);
    }
}
