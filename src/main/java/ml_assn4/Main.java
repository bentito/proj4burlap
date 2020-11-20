package ml_assn4;

import burlap.debugtools.RandomFactory;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static int SEED = 5;

    private static void executeProblem(ProblemAttempt problem, double qMaxDelta, int vipiPlotLength, int qPlotLength){
        List<AlgExperiment> algList = new ArrayList<>();
        //comment one at a time to screenshot each alg display (default graph viewer reuses same window so paths overlap)
        algList.add(AlgFactory.getVIAlg(0.99, 0.9, 1000));
        algList.add(AlgFactory.getPIAlg(0.99, 0.9, 1000, 100));
        algList.add(AlgFactory.getQAlg(0.99, 0.3, 0.1, qMaxDelta));

        problem.performExperiment(algList);

        problem.createLearningPlots(
                vipiPlotLength,
                AlgFactory.getVILearner(0.99, 1),
                AlgFactory.getPILearner(0.99, 1, 1000)
        );
        problem.createLearningPlots(
                qPlotLength,
                AlgFactory.getQLearner(0.99, 0.3, 0.1, qMaxDelta)
        );
    }

    public static void problem1(){
        ProblemAttempt problem = new GridWorldSolver();

        executeProblem(problem, 0.09, 50, 400);
    }

    public static void problem2(){
        ProblemAttempt problem = new GraphProblemSolver(1000);

        executeProblem(problem, 0.004, 12, 1500);
    }

    public static void main(String[] args) {

        RandomFactory.seedDefault(SEED);
        RandomFactory.seedMapped(0, SEED);
        System.setProperty("org.graphstream.ui", "swing");

        problem1();
        problem2();
    }
}
