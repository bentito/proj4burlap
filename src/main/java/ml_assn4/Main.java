package ml_assn4;

import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


public class Main {

    public static int SEED = 5;

    public static void main(String[] args) {
        boolean showViz = false;

        RandomFactory.seedDefault(SEED);
        RandomFactory.seedMapped(0, SEED);

        if (args.length>0)
            showViz = args[0].equals("viz");

        List<ProblemAttempt> problemList = new ArrayList<>();
        problemList.add(new GridWorldSolver());
//        problemList.add(new ml_assn4.CartPoleSolver());
//
//        List<BiFunction<Domain, State, Pair<ValueFunction, Policy>>> algList = new ArrayList<>();
//        algList.add(AlgFactory.getVIAlg(0.99, 0.001, 100));
//        algList.add(AlgFactory.getPIAlg(0.99, 0.001, 100, 100));
//        algList.add(AlgFactory.getQAlg(0.99, 0.3, 0.1, 0.1));

        GridWorldSolver s = new GridWorldSolver();
        List<BiFunction<Domain, State, LearningAgentFactory>> learningAlgList = new ArrayList<>();
        learningAlgList.add(AlgFactory.getVILearner(0.99, 0.001));
        learningAlgList.add(AlgFactory.getQLearner(0.99, 0.3, 0.1));

        for (ProblemAttempt attempt : problemList) {
            if (showViz){
                attempt.visualizeProblem();
            } else {
                attempt.createLearningPlots(learningAlgList, 350);
//                attempt.performExperiment(algList);
            }
        }
    }
}
