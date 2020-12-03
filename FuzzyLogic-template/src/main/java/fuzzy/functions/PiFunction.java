package fuzzy.functions;

//    __
// __/  \__
public class PiFunction implements FuzzyFunction{
    private int a;
    private int b;
    private int c;
    private int d;

    public PiFunction(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    @Override
    public double calculate(int value) {
        if(value < a){
            return 0.0;
        } else if (value <= b){
            return (value - a) / (b - a);
        } else if (value <= c) {
            return 1.0;
        } else if (value <= d) {
            return (d - value) / (d - c);
        } else {
            return 0.0;
        }
    }
}