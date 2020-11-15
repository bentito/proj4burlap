package ml_assn4.custom_algs;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;
import ml_assn4.AlgFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomValueIteration extends ValueIteration {

    State measureState = null;

    /**
     * Initializers the valueFunction.
     *
     * @param domain         the domain in which to plan
     * @param gamma          the discount factor
     * @param hashingFactory the state hashing factor to use
     * @param maxDelta       when the maximum change in the value function is smaller than this value, VI will terminate.
     * @param maxIterations  when the number of VI iterations exceeds this value, VI will terminate.
     */
    public CustomValueIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxIterations);
    }

    public void setMeasureState(State measureState){
//        this.measureState = this.hashingFactory.hashState(measureState);
        this.measureState = measureState;
    }

    @Override
    public void runVI() {
        if(!this.foundReachableStates){
            throw new RuntimeException("Cannot run VI until the reachable states have been found. Use the planFromState or performReachabilityFrom method at least once before calling runVI.");
        }

        Set<HashableState> states = valueFunction.keySet();
        List<Double> iterationRewards = new ArrayList<>();
        List<Double> iterationDeltas = new ArrayList<>();
        List<Long> iterationTimes = new ArrayList<>();

        int i;
        for(i = 0; i < this.maxIterations; i++){
            long startTime = System.nanoTime();
            double delta = 0.;
            for(HashableState sh : states){

                double v = this.value(sh);
                double maxQ = this.performBellmanUpdateOn(sh);
                delta = Math.max(Math.abs(maxQ - v), delta);

            }
            long endTime = System.nanoTime();

            if(measureState != null){
                GreedyQPolicy valuePolicy = new GreedyQPolicy(this);
                Episode e = PolicyUtils.rollout(valuePolicy, measureState, this.domain.getModel());
                iterationRewards.add(AlgFactory.calcReward(e));
                iterationDeltas.add(delta);
                iterationTimes.add(endTime - startTime);
            }

            if(delta < this.maxDelta){
                break; //approximated well enough; stop iterating
            }

        }

        DPrint.cl(this.debugCode, "Passes: " + i);
        System.out.println();

        DPrint.cl(this.debugCode, "Iteration, Reward, Delta, iterationTime");
        for (int j = 0; j < iterationRewards.size(); j++) {
            DPrint.cl(this.debugCode, j + " " + iterationRewards.get(j) + " " + iterationDeltas.get(j) + " " + iterationTimes.get(j));
        }

        System.out.println();

        this.hasRunVI = true;
    }
}
