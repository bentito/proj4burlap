package ml_assn4.custom_algs;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

public class CustomQLearning extends QLearning {
    double minQObserved = Double.POSITIVE_INFINITY;

    public CustomQLearning(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate) {
        super(domain, gamma, hashingFactory, qInit, learningRate);
    }

    @Override
    public GreedyQPolicy planFromState(State initialState) {
        if(this.model == null){
            throw new RuntimeException("QLearning (and its subclasses) cannot execute planFromState because a model is not specified.");
        }

        SimulatedEnvironment env = new SimulatedEnvironment(this.domain, initialState);

        int eCount = 0;
        do{
            this.runLearningEpisode(env, this.maxEpisodeSize);
            eCount++;
            env.resetEnvironment();
            if(maxQChangeInLastEpisode < minQObserved){
                minQObserved = maxQChangeInLastEpisode;
            }
            
        }while(eCount < numEpisodesForPlanning && maxQChangeInLastEpisode > maxQChangeForPlanningTermination);

        return new GreedyQPolicy(this);
    }
}
