package mcts.game;

import java.util.*;

public class Intersection {
    private int index;
    private List<Field> adjacentFields;
    private Set<Intersection> adjacentIntersections;

    public Intersection() {
        adjacentFields = new ArrayList<>();
        adjacentIntersections = new HashSet<>();
    }

    public Intersection(List<Field> adjacentFields, Set<Intersection> adjacentIntersections) {
        this.adjacentFields = adjacentFields;
        this.adjacentIntersections = adjacentIntersections;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Field> getAdjacentFields() {
        return adjacentFields;
    }

    public void setAdjacentFields(List<Field> adjacentFields) {
        this.adjacentFields = adjacentFields;
    }

    public Set<Intersection> getAdjacentIntersections() {
        return adjacentIntersections;
    }

    public void setAdjacentIntersections(Set<Intersection> adjacentIntersections) {
        this.adjacentIntersections = adjacentIntersections;
    }

    public void addAdjacentField(Field field){
        this.adjacentFields.add(field);
    }

    public void addAdjacentIntersection(Intersection intersection){
        this.adjacentIntersections.add(intersection);
        intersection.getAdjacentIntersections().add(this);
    }

    public boolean isConnected(int playerId, HashMap<ValuesXY, Integer> indexXYRoads){
        boolean isConnected = false;
        for(Intersection i : this.adjacentIntersections){
            if(indexXYRoads.get(new ValuesXY(this.index, i.getIndex())) == playerId){
                isConnected = true;
            }
        }
        return isConnected;
    }

    public int numberOfRoads(int playerId, HashMap<ValuesXY, Integer> indexXYRoads){
        int counter = 0;
        for(Intersection i : this.adjacentIntersections){
            if(indexXYRoads.get(new ValuesXY(this.index, i.getIndex())) == playerId){
                counter++;
            }
        }
        return counter;
    }

    public boolean adjacentToCity(HashMap<Integer, City>[] indexCities){
        for(Intersection i : this.adjacentIntersections){
            if(indexCities[0].containsKey(this.getIndex()) || indexCities[1].containsKey(this.getIndex())){
                return true;
            }
        }
        return false;
    }
}
