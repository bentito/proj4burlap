import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.core.Domain;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import java.util.function.Function;


public class AgentFactory {

    public static Function<Domain, LearningAgentFactory> getQLearner(final double gamma, final double qInit, final double learningRate){
        return (domain) -> new LearningAgentFactory() {
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
}
