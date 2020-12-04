package fuzzy;


import fuzzy.functions.FuzzyFunction;

public class FuzzySet {
    private Domain domain;
    private FuzzyFunction function;

    public FuzzySet(Domain domain, FuzzyFunction function) {
        this.domain = domain;
        this.function = function;
    }

    public Domain getDomain() {
        return domain;
    }

    public FuzzyFunction getFunction() {
        return function;
    }

    public double getValueAt(int value){
        return function.calculate(value);
    }
}
