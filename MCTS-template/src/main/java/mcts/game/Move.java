package mcts.game;

public class Move {
    // TODO implement move
    private MoveType type;
    private int intersection1;
    private int intersection2;

    public Move(MoveType type, int intersection1, int intersection2) {
        this.type = type;
        this.intersection1 = intersection1;
        this.intersection2 = intersection2;
    }

    public Move(MoveType type, int intersection1) {
        this.type = type;
        this.intersection1 = intersection1;
        this.intersection2 = -1;
    }

    public Move(MoveType type) {
        this.type = type;
        this.intersection1 = -1;
        this.intersection2 = -1;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public int getIntersection1() {
        return intersection1;
    }

    public void setIntersection1(int intersection1) {
        this.intersection1 = intersection1;
    }

    public int getIntersection2() {
        return intersection2;
    }

    public void setIntersection2(int intersection2) {
        this.intersection2 = intersection2;
    }
}
