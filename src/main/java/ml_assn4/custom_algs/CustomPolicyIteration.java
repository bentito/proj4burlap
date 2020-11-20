package ml_assn4.custom_algs;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;

import java.util.Set;

public class CustomPolicyIteration extends PolicyIteration implements LearningAgent {
    State initialState;

    public CustomPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxEvaluationIterations, maxPolicyIterations);
    }

    public CustomPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta) {
        super(domain, gamma, hashingFactory, maxDelta, 0, 0);
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

        if(this.performReachabilityFrom(initialState)){
            DPrint.cl(this.debugCode, "reachability found");
        }

        Set <HashableState> states = valueFunction.keySet();

        for (int i = 0; i < maxSteps; i++) {
            double delta = 0.;
            for(HashableState sh : states){

                double v = this.value(sh);
                double maxQ = this.performFixedPolicyBellmanUpdateOn(sh, this.evaluativePolicy);
                delta = Math.max(Math.abs(maxQ - v), delta);

            }
        }

        return constructEpisode(env);
    }

    private Episode constructEpisode(Environment env){
        this.evaluativePolicy = new GreedyQPolicy(this.getCopyOfValueFunction());
        return PolicyUtils.rollout(this.evaluativePolicy, env, 5000);
    }
}
