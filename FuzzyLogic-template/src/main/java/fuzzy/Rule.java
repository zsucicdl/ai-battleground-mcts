package fuzzy;


public class Rule {
    private FuzzySet[] conditionals;
    private FuzzySet statement;

    public Rule(FuzzySet[] conditionals, FuzzySet statement){
        this.conditionals = conditionals;
        this.statement = statement;
    }

    public FuzzySet[] getConditionals() {
        return conditionals;
    }

    public FuzzySet getStatement() {
        return statement;
    }

    public double[] apply(int[] values){
        double minValue = Double.MAX_VALUE;
        for(int i = 0; i < conditionals.length; i++) {
            double value = conditionals[i].getValueAt(values[i]);
            minValue = Math.min(minValue, value);
        }

        double[] result = new double[statement.getDomain().getLength()];
        for(int i = 0; i < result.length; i++){
            double theValue = statement.getValueAt(i + statement.getDomain().getFirst());
            result[i] = Math.min(minValue, theValue);
        }
        return result;
    }
}
