package mcts.game;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private List<Field> adjacentFields;
    private List<Intersection> adjacentIntersections;

    public Intersection() {
        adjacentFields = new ArrayList<>();
        adjacentIntersections = new ArrayList<>();
    }

    public Intersection(List<Field> adjacentFields, List<Intersection> adjacentIntersections) {
        this.adjacentFields = adjacentFields;
        this.adjacentIntersections = adjacentIntersections;
    }

    public List<Field> getAdjacentFields() {
        return adjacentFields;
    }

    public void setAdjacentFields(List<Field> adjacentFields) {
        this.adjacentFields = adjacentFields;
    }

    public List<Intersection> getAdjacentIntersections() {
        return adjacentIntersections;
    }

    public void setAdjacentIntersections(List<Intersection> adjacentIntersections) {
        this.adjacentIntersections = adjacentIntersections;
    }

    public void addAdjacentField(Field field){
        this.adjacentFields.add(field);
    }

    public void addAdjacentIntersection(Intersection intersection){
        this.adjacentIntersections.add(intersection);
    }
}
