package mcts.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Intersection {
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
        intersection.addAdjacentIntersection(this);
    }
}
