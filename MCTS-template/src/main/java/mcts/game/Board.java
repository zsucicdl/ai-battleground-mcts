package mcts.game;

import java.util.HashMap;
import java.util.List;

public class Board {
    private Builder player1;
    private Builder player2;

    private City[] cities1;
    private City[] cities2;

    private HashMap<ValuesXY, Field> positionFields;
    private HashMap<Integer, Intersection> indexIntersections;
    private HashMap<ValuesXY, Boolean> intersectionRoads;

    /*
    *  private Builder[] players;
    * private City[][] cities;
    *
    *  */

    public Board(Builder player1, Builder player2, City[] cities1, City[] cities2,
                 HashMap<ValuesXY, Field> positionFields, HashMap<Integer, Intersection> indexIntersections,
                 HashMap<ValuesXY, Boolean> intersectionRoads) {
        this.player1 = player1;
        this.player2 = player2;
        this.cities1 = cities1;
        this.cities2 = cities2;
        this.positionFields = positionFields;
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

    public Board copy(){
        // TODO implement
        City[] newCities1 = new City[] {this.cities1[0].copy(), this.cities1[1].copy()};
        City[] newCities2 = new City[] {this.cities2[0].copy(), this.cities2[1].copy()};
        HashMap<ValuesXY, Field> newPositionFields = new HashMap<>(this.positionFields);
        HashMap<Integer, Intersection> newIndexIntersections = new HashMap<>(this.indexIntersections);
        HashMap<ValuesXY, Boolean> newIntersectionRoads = new HashMap<>(this.intersectionRoads);
        return new Board(player1.copy(), player2.copy(), newCities1, newCities2, newPositionFields, newIndexIntersections, newIntersectionRoads);
    }

    public static Board initBoard(List<List<Integer>> intersectionToIntersection, List<List<ValuesXY>> intersectionToField){
        HashMap<ValuesXY, Field> positionFields = new HashMap<>();
        HashMap<Integer, Intersection> indexIntersections = new HashMap<>();
        HashMap<ValuesXY, Boolean> intersectionRoads = new HashMap<>();

        int n = intersectionToField.size();
        for(int i = 0; i < n; i++){

        }
    }
}
