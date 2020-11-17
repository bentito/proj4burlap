package ml_assn4;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.discretized.DiscretizingHashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import javafx.util.Pair;
import ml_assn4.custom_algs.CustomValueIteration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


public class AlgFactory {

    public static double calcReward(Episode e){
        double myRewards = 0;
        //sum all rewards
        for (int i = 0; i<e.rewardSequence.size(); i++) {
            myRewards += e.rewardSequence.get(i);
        }
        return myRewards;
    }

    // region learning agents

    public static BiFunction<Domain, State, LearningAgentFactory> getQLearner(double gamma, double qInit, double learningRate){
        return (domain, initialState) -> new LearningAgentFactory() {
            //set up the state hashing system for looking up states
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//            final DiscretizingHashableStateFactory hashingFactory = new DiscretizingHashableStateFactory(1.);

            public String getAgentName() {
                return "Q-learning";
            }

            public LearningAgent generateAgent() {
                return new QLearning((SADomain) domain, gamma, hashingFactory, qInit, learningRate);
            }
        };
    }

    public static BiFunction<Domain, State, LearningAgentFactory> getVILearner(double gamma, double maxDelta){
        return (domain, initialState) -> new LearningAgentFactory() {
            //set up the state hashing system for looking up states
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//            final DiscretizingHashableStateFactory hashingFactory = new DiscretizingHashableStateFactory(0.1);

            public String getAgentName() {
                return "Value Iteration";
            }

            public LearningAgent generateAgent() {
                CustomValueIteration agent = new CustomValueIteration((SADomain)domain, gamma, hashingFactory, maxDelta);
                agent.setInitialState(initialState);
                return agent;
            }
        };
    }

    // endregion learning agents

    // region iterative algs

    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getVIAlg(double gamma, double maxDelta, int maxIterations){
        return (domain, initialState) -> {
            SADomain currentDomain = (SADomain) domain;
            // value iteration
            SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

            CustomValueIteration valuePlanner = new CustomValueIteration(currentDomain, gamma, hashingFactory, maxDelta, maxIterations);
            valuePlanner.toggleReachabiltiyTerminalStatePruning(true);
            GreedyQPolicy valuePolicy = valuePlanner.planFromState(initialState);

            return new Pair<>(valuePlanner, valuePolicy);
        };
    }

    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getPIAlg(double gamma, double maxDelta, int maxEvalIterations, int maxPolicyIterations){
        return (domain, initialState) -> {
            // policy iteration
            SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
            PolicyIteration policyPlanner = new PolicyIteration((SADomain)domain, gamma, hashingFactory, maxDelta, maxEvalIterations, maxPolicyIterations);

            GreedyQPolicy policyPolicy = policyPlanner.planFromState(initialState);

            return new Pair<>(policyPlanner, policyPolicy);
        };
    }

    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getQAlg(double gamma, double qInit, double learningRate, double epsilon){
        return (domain, initialState) -> {
            SADomain currentDomain = (SADomain) domain;
            // qlearning agent
            BiFunction<Domain, State, LearningAgentFactory> qAgentGenerator = AlgFactory.getQLearner(gamma, qInit, learningRate);
            LearningAgentFactory qAgentFactory = qAgentGenerator.apply(currentDomain, initialState);
            QLearning qAgent = (QLearning) qAgentFactory.generateAgent();
            Policy qPolicy = new EpsilonGreedy(qAgent, epsilon);
            qAgent.setLearningPolicy(qPolicy);

            //initial state generator
            final ConstantStateGenerator sg = new ConstantStateGenerator(initialState);

            //define learning environment
            SimulatedEnvironment env = new SimulatedEnvironment((SADomain) domain, sg);

            //run learning for 50 episodes
            for(int i = 0; i < 2500; i++){
                Episode e = qAgent.runLearningEpisode(env);
//            e.write(outputPath + "ql_" + i);
//            System.out.println(i + ": " + e.maxTimeStep());
                //reset environment for next learning episode
                env.resetEnvironment();
            }

//        PolicyUtils.rollout(p, initialState, currentDomain.getModel()).write(outputPath + "vi");

            return new Pair<>((ValueFunction)qAgent, qPolicy);
        };
    }

    // endregion iterative algs
}
