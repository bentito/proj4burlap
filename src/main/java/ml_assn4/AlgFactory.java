package ml_assn4;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.Policy;
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
import burlap.statehashing.simple.SimpleHashableStateFactory;
import javafx.util.Pair;

import java.util.function.BiFunction;


public class AlgFactory {

    public static LearningAgentFactory getQLearner(Domain domain, final double gamma, final double qInit, final double learningRate){
        return new LearningAgentFactory() {
            //set up the state hashing system for looking up states
            final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

            public String getAgentName() {
                return "Q-learning";
            }

            public LearningAgent generateAgent() {
                return new QLearning((SADomain) domain, gamma, hashingFactory, qInit, learningRate);
            }
        };
    }


    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getVIAlg(double gamma, double maxDelta, int maxIterations){
        return (domain, initialState) -> {
            // value iteration
            SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
            Planner valuePlanner = new ValueIteration((SADomain)domain, gamma, hashingFactory, maxDelta, maxIterations);
            Policy valuePolicy = valuePlanner.planFromState(initialState);
//        PolicyUtils.rollout(p, initialState, currentDomain.getModel()).write(outputPath + "vi");

            return new Pair<>((ValueFunction)valuePlanner, valuePolicy);
        };
    }

    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getPIAlg(double gamma, double maxDelta, int maxEvalIterations, int maxPolicyIterations){
        return (domain, initialState) -> {
            // policy iteration
            SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
            Planner policyPlanner = new PolicyIteration((SADomain)domain, gamma, hashingFactory, maxDelta, maxEvalIterations, maxPolicyIterations);
            Policy policyPolicy = policyPlanner.planFromState(initialState);
//        PolicyUtils.rollout(p, initialState, currentDomain.getModel()).write(outputPath + "vi");

            return new Pair<>((ValueFunction)policyPlanner, policyPolicy);
        };
    }

    public static BiFunction<Domain, State, Pair<ValueFunction, Policy>> getQAlg(double gamma, double qInit, double learningRate, double epsilon){
        return (domain, initialState) -> {
            // qlearning agent
//            LearningAgentFactory qAgentFactory = AgentFactory.getQLearner(domain, .99, 0.3, 0.1); epsilon=0.1
            LearningAgentFactory qAgentFactory = AlgFactory.getQLearner(domain, gamma, qInit, learningRate);
            LearningAgent qAgent = qAgentFactory.generateAgent();
            Policy qPolicy = new EpsilonGreedy((QLearning)qAgent, epsilon);
            ((QLearning) qAgent).setLearningPolicy(qPolicy);

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
}
