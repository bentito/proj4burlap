package proj4burlap.custom_algs;

import burlap.behavior.policy.*;
import burlap.behavior.singleagent.learnfromdemo.apprenticeship.ApprenticeshipLearning;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

import javax.management.RuntimeErrorException;

public class CustomQLearning extends QLearning {
    double minQObserved = Double.POSITIVE_INFINITY;

    public CustomQLearning(SADomain domain, double gamma, HashableStateFactory hashingFactory, double qInit, double learningRate, String policyType, float policyKnob) {
        super(domain, gamma, hashingFactory, qInit, learningRate);

        switch (policyType) {
            case "boltzmann": {
                BoltzmannQPolicy policy = new BoltzmannQPolicy(this, policyKnob);
                this.setLearningPolicy(policy);
                break;
            }
            case "egreedy": {
                EpsilonGreedy policy = new EpsilonGreedy(this, policyKnob);
                this.setLearningPolicy(policy);
                break;
            }
            case "random": {
                RandomPolicy policy = new RandomPolicy(domain);
                this.setLearningPolicy(policy);
                break;
            }
            case "apprentice": {
                this.setLearningPolicy(ApprenticeshipLearning.StationaryRandomDistributionPolicy.generateRandomPolicy(domain));
                break;
            }
            case "detgreedy": {
                GreedyDeterministicQPolicy policy = new GreedyDeterministicQPolicy(this);
                this.setLearningPolicy(policy);
                break;
            }
            default:
                throw new RuntimeErrorException(new Error("Must choose a policy type"));
        }
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
