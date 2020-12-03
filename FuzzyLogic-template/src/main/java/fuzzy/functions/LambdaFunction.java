package fuzzy.functions;

//
// __/\__
public class LambdaFunction implements FuzzyFunction {
    private int a;
    private int b;
    private int c;

    public LambdaFunction(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double calculate(int value) {
        if (value < a) {
            return 0.0;
        } else if (value <= b){
            return((double) value - a)/(b -a);
        } else if(value <= c) {
            return ((double) c - value) / (c - b);
        } else{
            return 0.0;
        }
    }
}

