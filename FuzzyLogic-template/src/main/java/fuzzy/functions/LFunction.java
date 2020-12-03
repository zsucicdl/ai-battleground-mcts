package fuzzy.functions;

// ___
//    \___
public class LFunction implements FuzzyFunction {
    private int a;
    private int b;

    public LFunction(int a, int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public double calculate(int value) {
        if (value < a){
            return 1.0;
        } else if(value <= b){
            return ((double) b - value) / (b - a);
        } else {
            return 0.0;
        }
    }
}
