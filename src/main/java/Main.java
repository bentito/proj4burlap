import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        boolean shouldExperiment = false;

        if (args.length>0)
            shouldExperiment = args[0].equals("run");

        List<ProblemAttempt> problemList = new ArrayList<>();
        problemList.add(new GridWorldSolver());
//        problemList.add(new CartPoleSolver());

        for (ProblemAttempt attempt : problemList) {
            if (shouldExperiment){
                //throw a q-learner at the grid world problem
                attempt.performExperiment(AgentFactory.getQLearner(.99, 0.3, 0.1));

            } else {
                attempt.visualizeProblem();
            }
        }
    }
}
