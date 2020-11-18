package ml_assn4.custom_algs;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;
import ml_assn4.AlgFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomValueIteration extends ValueIteration implements LearningAgent {
    State initialState;
    boolean hasConverged = false;

    public CustomValueIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxIterations);
    }

    public CustomValueIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta) {
        super(domain, gamma, hashingFactory, maxDelta, 0);
    }

    public void setInitialState(State initialState){
        this.initialState = initialState;
    }

    @Override
    public Episode runLearningEpisode(Environment env) {
        return runLearningEpisode(env, 1);
    }

    @Override
    public Episode runLearningEpisode(Environment env, int maxSteps) {
        if(hasConverged){
            return constructEpisode(env);
        }
        if(this.performReachabilityFrom(initialState)){
            DPrint.cl(this.debugCode, "reachability found");
        }

        Set<HashableState> states = valueFunction.keySet();

        for(int i = 0; i < maxSteps; i++){
            double delta = 0.;
            for(HashableState sh : states){
                double v = this.value(sh);
                double maxQ = this.performBellmanUpdateOn(sh);
                delta = Math.max(Math.abs(maxQ - v), delta);
            }

            if(delta < this.maxDelta){
                hasConverged = true;
            }
        }
        return constructEpisode(env);
    }

    private Episode constructEpisode(Environment env){
        GreedyQPolicy valuePolicy = new GreedyQPolicy(this);
        return PolicyUtils.rollout(valuePolicy, env, 5000);
    }
}
