package mcts.game;

public class City {
    private Intersection intersection;
    private int level;

    public City(Intersection intersection) {
        this.intersection = intersection;
    }

    public City(Intersection intersection, int level) {
        this.intersection = intersection;
        this.level = level;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public int getLevel() {
        return level;
    }

    public void upgrade(){
        if (this.level == 1){
            this.level++;
        }
    }

    public City copy(){
        return new City(this.intersection, this.level);
    }
}
