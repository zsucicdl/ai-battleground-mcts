package fuzzy.rules;

import fuzzy.Domain;
import fuzzy.FuzzySet;
import fuzzy.Rule;
import fuzzy.functions.GammaFunction;
import fuzzy.functions.LFunction;
import fuzzy.functions.LambdaFunction;
import fuzzy.functions.PiFunction;

public class Rules {
    public static final Domain DISTANCE_DOMAIN = new Domain(0, 300);
    public static final Domain SPEED_DOMAIN = new Domain(-50, 50);

    public static final FuzzySet CLOSE_DISTANCE_SET = new FuzzySet(DISTANCE_DOMAIN, new LFunction(0, 200));
    public static final FuzzySet MEDIUM_DISTANCE_SET = new FuzzySet(DISTANCE_DOMAIN, new LambdaFunction(100, 200, 300));
    public static final FuzzySet FAR_DISTANCE_SET =  new FuzzySet(DISTANCE_DOMAIN, new GammaFunction(200, 300));

    public static final FuzzySet FORWARD_FAST_SET = new FuzzySet(SPEED_DOMAIN, new GammaFunction(30, 40));
    public static final FuzzySet HOLD_STILL_SET = new FuzzySet(SPEED_DOMAIN, new PiFunction(-10, -9, 9, 10));
    public static final FuzzySet BACKWARD_FAST_SET = new FuzzySet(SPEED_DOMAIN, new LFunction(-40, -30));

    public static final Rule IF_CLOSE_FORWARD_FAST = new Rule(new FuzzySet[] {CLOSE_DISTANCE_SET}, FORWARD_FAST_SET);
    public static final Rule IF_MEDIUM_STAY = new Rule(new FuzzySet[] {MEDIUM_DISTANCE_SET}, HOLD_STILL_SET);
    public static final Rule IF_FAR_BACKWARD_FAST = new Rule(new FuzzySet[] {FAR_DISTANCE_SET}, BACKWARD_FAST_SET);
}
