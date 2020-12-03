package fuzzy;

public class Domain {
    private int first;
    private int last;

    public Domain(int first, int last){
        this.first = first;
        this.last = last;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public int getLength(){
        return last - first + 1;
    }

    public double getPercentage(int value){
        return ((double) value - first) / (last - first);
    }

    public int getValue(double percentage){
        return (int) Math.round(percentage * (last - first) + first);
    }
}
