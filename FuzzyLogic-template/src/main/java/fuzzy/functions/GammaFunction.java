package fuzzy.functions;

//     ___
// ___/
public class GammaFunction implements FuzzyFunction {
    private int a;
    private int b;

    public GammaFunction(int a, int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public double calculate(int value) {
        if (value < a){
            return 0.0;
        } else if(value <= b){
            return ((double) value - a) / (b - a);
        } else {
            return 1.0;
        }
    }
}