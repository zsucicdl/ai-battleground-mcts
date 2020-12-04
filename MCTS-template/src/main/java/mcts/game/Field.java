package mcts.game;

public class Field {
    private Resource resource;
    private int weight;

    public Field(Resource resource, int weight) {
        this.resource = resource;
        this.weight = weight;
    }

    public Field(){

    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
