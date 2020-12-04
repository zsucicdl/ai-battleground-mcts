package fuzzy;

import java.util.ArrayList;
import java.util.List;

public class System {
    private List<Rule> rules;
    private Domain statementDomain;

    public System(){
        this.rules = new ArrayList<Rule>();
    }

    public void addRule(Rule rule){
        this.rules.add(rule);
        if(statementDomain == null){
            statementDomain = rule.getStatement().getDomain();
        }
    }

    public int conclude(int[] values){
        int counter = 0;
        double[] finalResult = new double[0];
        for(Rule rule : rules){
            if(counter == 0){
                finalResult = rule.apply(values);
            } else {
                double[] result = rule.apply(values);
                for(int i = 0; i < finalResult.length; i++){
                    finalResult[i] = Math.max(finalResult[i], result[i]);
                }
            }
            counter++;
        }

        int score = defuzzy(finalResult);
        return score;
    }

    private int defuzzy(double[] result) {
        double numerator = 0.0;
        double denumerator = 0.0;
        for(int i = 0; i < result.length; i++) {
            numerator += result[i] * (i + statementDomain.getFirst());
            denumerator += result[i];
        }
        if (Math.abs(numerator) < 1e-8){
            return 0;
        }
        return (int) Math.round(numerator / denumerator);
    }
}
