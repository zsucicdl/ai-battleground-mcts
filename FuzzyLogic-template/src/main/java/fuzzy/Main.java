package fuzzy;

import fuzzy.functions.GammaFunction;
import fuzzy.functions.LFunction;
import fuzzy.functions.LambdaFunction;
import fuzzy.rules.Rules;

import java.io.BufferedReader;
import java.io.InputStreamReader;

// escape fuzzy
// the closer the target is behind me, the faster i need to run away

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(java.lang.System.in))) {
            String line = "";

            System s = new System();
            s.addRule(Rules.IF_CLOSE_FORWARD_FAST);
            s.addRule(Rules.IF_MEDIUM_STAY);
            s.addRule(Rules.IF_FAR_BACKWARD_FAST);

            while (!line.equals("exit")){
                line = reader.readLine().strip();
                int[] values = new int[] {Integer.parseInt(line)};
                int result = s.conclude(values);
                java.lang.System.out.println(result);
            }
        } catch (Exception e){
        }
    }
}
