package mcts.game;

import java.util.HashMap;

public class Builder {
    private int playerId;
    private Intersection currentIntersection;
    private HashMap<Resource, Integer> availableResources = new HashMap<>();

    private int points;

    public Builder(int playerId){
        this.playerId = playerId;
        availableResources.put(Resource.SHEEP, 0);
        availableResources.put(Resource.WOOD, 0);
        availableResources.put(Resource.IRON, 0);
        availableResources.put(Resource.CLAY, 0);
        availableResources.put(Resource.WHEAT, 0);
    }

    public Builder(int playerId, Intersection currentIntersection, HashMap<Resource, Integer> availableResources, int points) {
        this.playerId = playerId;
        this.currentIntersection = currentIntersection;
        this.availableResources = new HashMap<>(availableResources);
        this.points = points;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Intersection getCurrentIntersection() {
        return currentIntersection;
    }

    public void setCurrentIntersection(Intersection currentIntersection) {
        this.currentIntersection = currentIntersection;
    }

    public HashMap<Resource, Integer> getAvailableResources() {
        return availableResources;
    }

    public void setAvailableResources(HashMap<Resource, Integer> availableResources) {
        this.availableResources = availableResources;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void gainPoint() {
        points++;
    }

    public Builder copy(){
        return new Builder(playerId, currentIntersection, availableResources, points);
    }

    public int getNoOfResources() {
        int noOfRes = 0;
        for(Integer value : availableResources.values()){
            noOfRes += value;
        }
        return noOfRes;
    }

}
