package ml_assn4;

import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;


public class Plotter {

    public static void plot(final SADomain domain, State initialState, int trialLength, LearningAgentFactory...agentFactory){

        //initial state generator
        final ConstantStateGenerator sg = new ConstantStateGenerator(initialState);
//        final RandomStartStateGenerator sg = new RandomStartStateGenerator(domain, initialState);

        //define learning environment
        SimulatedEnvironment env = new SimulatedEnvironment(domain, sg);

        //define experiment
        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 10, trialLength, agentFactory);

        exp.setUpPlottingConfiguration(
                500,
                250,
                2,
                1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_REWARD_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.STEPS_PER_EPISODE
//                PerformanceMetric.MEDIAN_EPISODE_REWARD,
//                PerformanceMetric.CUMULATIVE_REWARD_PER_STEP,
        );

        //start experiment
        exp.startExperiment();
    }

}