package mcts.game;

public class Move {
    // TODO implement move
    private MoveType type;
    private int index1;
    private int index2;

    public Move(MoveType type, int index1, int index2) {
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
    }

    public Move(MoveType type, int index1) {
        this.type = type;
        this.index1 = index1;
        this.index2 = -1;
    }

    public Move(MoveType type) {
        this.type = type;
        this.index1 = -1;
        this.index2 = -1;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    public static Move fromString(String string){
        String[] words = string.split(" ");
        String type = words[0].toLowerCase();
        int value1, value2;
        MoveType moveType = null;
        if(type.equals("initial")){
            moveType = MoveType.INITIAL;
            value1 = Integer.parseInt(words[1]);
            value2 = Integer.parseInt(words[2]);
        } else if(type.equals("move") || type.equals("buildroad") || type.equals("upgradetown")) {
            switch (type) {
                case "move":
                    moveType = MoveType.MOVE;
                    break;
                case "buildroad":
                    moveType = MoveType.BUILD_ROAD;
                    break;
                case "upgradetown":
                    moveType = MoveType.UPGRADE_TOWN;
                    break;
            }
            value1 = Integer.parseInt(words[1]);
            value2 = -1;
        } else {
            switch (type) {
                case "buildtown":
                    moveType = MoveType.BUILD_TOWN;
                    break;
                case "empty":
                    moveType = MoveType.EMPTY;
                    break;
            }
            value1 = -1;
            value2 = -1;
        }
        return new Move(moveType, value1, value2);
    }

    @Override
    public String toString() {
       switch (this.type){
           case INITIAL:
               return "initial " + this.index1 + " " + this.index2;
           case MOVE:
               return "move " + this.index1;
           case BUILD_ROAD:
               return "buildroad " + this.index1;
           case BUILD_TOWN:
               return "buildtown";
           case UPGRADE_TOWN:
               return "upgradetown " + this.index1;
           default:
               return "empty";
       }
    }
}
