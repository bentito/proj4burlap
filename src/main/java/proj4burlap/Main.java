package proj4burlap;

import burlap.debugtools.RandomFactory;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static int SEED = 7;

    private static void executeProblem(ProblemAttempt problem, double qMaxDelta, int vipiPlotLength, int qPlotLength){
        List<AlgExperiment> algList = new ArrayList<>();
        //comment one at a time to screenshot each alg display (default graph viewer reuses same window so paths overlap)
//        algList.add(AlgFactory.getVIAlg(0.99, 0.9, 1000));
//        algList.add(AlgFactory.getPIAlg(0.99, 0.9, 1000, 100));
//        String policyType = "boltzmann"; // knob is temperature, low value = greedy, high = random
//        String policyType = "egreedy"; // knob is epsilon, low value = greedy, high = random
        String policyType = "detgreedy";
//        String policyType = "random"; // always just random choice for tied actions, no policy knob
//        String policyType = "apprentice";
        float policyKnob = 0.1F;

        algList.add(AlgFactory.getQAlg(0.99, 0.3, 0.1, qMaxDelta, policyType, policyKnob));

        problem.performExperiment(algList);

        problem.createLearningPlots(
                vipiPlotLength,
                AlgFactory.getVILearner(0.99, 1),
                AlgFactory.getPILearner(0.99, 1, 1000)
        );
        problem.createLearningPlots(
                qPlotLength,
                AlgFactory.getQLearner(0.99, 0.3, 0.1, qMaxDelta, policyType, policyKnob)
        );
    }

    public static void problem1(){
        ProblemAttempt problem = new GridWorldSolver();

        executeProblem(problem, 0.09, 50, 400);
    }

    public static void problem2(){
        ProblemAttempt problem = new GraphProblemSolver(100);

        executeProblem(problem, 0.06, 12, 250);
    }

    public static void main(String[] args) {

        RandomFactory.seedDefault(SEED);
        RandomFactory.seedMapped(0, SEED);
        System.setProperty("org.graphstream.ui", "swing");

        problem1(); // grid world
//        problem2(); // graph problem
    }
}
