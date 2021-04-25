package proj4burlap;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import proj4burlap.custom_algs.CustomPolicyIteration;
import proj4burlap.custom_algs.CustomQLearning;
import proj4burlap.custom_algs.CustomValueIteration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiFunction;


abstract class AlgExperiment{
    abstract String getAlgName();
    abstract Pair<ValueFunction, Policy> performExperiment(Domain domain, State initialState);
}


public class AlgFactory {

    // region learning agents

    public static BiFunction<Domain, State, LearningAgentFactory> getQLearner(double gamma, double qInit, double learningRate, double maxDelta, String policyType, float policyKnob){
        return (domain, initialState) -> new LearningAgentFactory() {
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

            public String getAgentName() {
                return "Q-learning";
            }

            public LearningAgent generateAgent() {
                QLearning agent = new CustomQLearning((SADomain) domain, gamma, hashingFactory, qInit, learningRate, policyType, policyKnob);
                agent.setMaxQChangeForPlanningTerminaiton(maxDelta);
                agent.initializeForPlanning((int)Double.POSITIVE_INFINITY);
                return agent;
            }
        };
    }

    public static BiFunction<Domain, State, LearningAgentFactory> getVILearner(double gamma, double maxDelta){
        return (domain, initialState) -> new LearningAgentFactory() {
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

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

    public static BiFunction<Domain, State, LearningAgentFactory> getPILearner(double gamma, double maxDelta, int maxIter){
        return (domain, initialState) -> new LearningAgentFactory() {
            //set up the state hashing system for looking up states
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
//            final DiscretizingHashableStateFactory hashingFactory = new DiscretizingHashableStateFactory(0.1);

            public String getAgentName() {
                return "Policy Iteration";
            }

            public LearningAgent generateAgent() {
                CustomPolicyIteration agent = new CustomPolicyIteration((SADomain)domain, gamma, hashingFactory, maxDelta, maxIter, maxIter);
                agent.setInitialState(initialState);
                return agent;
            }
        };
    }

    // endregion learning agents

    // region iterative algs


    public static AlgExperiment getVIAlg(double gamma, double maxDelta, int maxIterations){
        return new AlgExperiment() {
            @Override
            String getAlgName() {
                return "Value Iteration";
            }

            @Override
            Pair<ValueFunction, Policy> performExperiment(Domain domain, State initialState) {
                System.out.println("Value Iteration");
                SADomain currentDomain = (SADomain) domain;
                // value iteration
                SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

                CustomValueIteration valuePlanner = new CustomValueIteration(currentDomain, gamma, hashingFactory, maxDelta, maxIterations);
                valuePlanner.toggleReachabiltiyTerminalStatePruning(true);
                GreedyQPolicy valuePolicy = valuePlanner.planFromState(initialState);

                return Pair.of(valuePlanner, valuePolicy);
            }
        };
    }

    public static AlgExperiment getPIAlg(double gamma, double maxDelta, int maxEvalIterations, int maxPolicyIterations){
        return new AlgExperiment() {
            @Override
            String getAlgName() {
                return "Policy Iteration";
            }

            @Override
            Pair<ValueFunction, Policy> performExperiment(Domain domain, State initialState) {
                System.out.println("Policy Iteration");
                // policy iteration
                SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
                PolicyIteration policyPlanner = new CustomPolicyIteration((SADomain)domain, gamma, hashingFactory, maxDelta, maxEvalIterations, maxPolicyIterations);

                GreedyQPolicy policyPolicy = policyPlanner.planFromState(initialState);

                return Pair.of(policyPlanner, policyPolicy);
            }
        };
    }

    public static AlgExperiment getQAlg(double gamma, double qInit, double learningRate, double maxDelta, String policyType, float policyKnob){
        return new AlgExperiment() {
            @Override
            String getAlgName() {
                return "Q-Learning";
            }

            @Override
            Pair<ValueFunction, Policy> performExperiment(Domain domain, State initialState) {
                System.out.println("Q-Learning");
                SADomain currentDomain = (SADomain) domain;

                BiFunction<Domain, State, LearningAgentFactory> qAgentGenerator = AlgFactory.getQLearner(gamma, qInit, learningRate, maxDelta, policyType, policyKnob);
                LearningAgentFactory qAgentFactory = qAgentGenerator.apply(currentDomain, initialState);
                QLearning qAgent = (QLearning) qAgentFactory.generateAgent();

                Policy qPolicy = qAgent.planFromState(initialState);

                return Pair.of(qAgent, qPolicy);
            }
        };
    }

    // endregion iterative algs
}
