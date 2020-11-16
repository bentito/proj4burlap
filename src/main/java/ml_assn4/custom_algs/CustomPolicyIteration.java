package ml_assn4.custom_algs;

import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.Set;

public class CustomPolicyIteration extends PolicyIteration {
    private int iterCounter = 0;

    public CustomPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxEvaluationIterations, maxPolicyIterations);
    }

    public CustomPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxPIDelta, double maxEvalDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxPIDelta, maxEvalDelta, maxEvaluationIterations, maxPolicyIterations);
    }

    @Override
    protected double evaluatePolicy() {

        if(!this.foundReachableStates){
            throw new RuntimeException("Cannot run VI until the reachable states have been found. Use planFromState method at least once or instead.");
        }

        double maxChangeInPolicyEvaluation = Double.NEGATIVE_INFINITY;

        Set<HashableState> states = valueFunction.keySet();

        int i;
        for(i = 0; i < this.maxIterations; i++){

            double delta = 0.;
            for(HashableState sh : states){

                double v = this.value(sh);
                double maxQ = this.performFixedPolicyBellmanUpdateOn(sh, this.evaluativePolicy);
                delta = Math.max(Math.abs(maxQ - v), delta);

            }

            maxChangeInPolicyEvaluation = Math.max(delta, maxChangeInPolicyEvaluation);

            if(delta < this.maxEvalDelta){
                i++;
                break; //approximated well enough; stop iterating
            }

        }

        DPrint.cl(this.debugCode, "Iterations in inner VI for policy eval: " + i);
        this.totalValueIterations += i;

        return maxChangeInPolicyEvaluation;

    }
}
