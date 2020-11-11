import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        boolean shouldExperiment = false;

        if (args.length>0)
            shouldExperiment = args[0].equals("run");

        List<ProblemAttempt> problemList = new ArrayList<>();
//        problemList.add(new GridWorldSolver());
        problemList.add(new CartPoleSolver());

        for (ProblemAttempt attempt : problemList) {
            if (shouldExperiment){
                //throw a q-learner at the grid world problem
                attempt.performExperiment((problemDomain) ->
//                        AgentFactory.getQLearner((OOSADomain) problemDomain, .99, 0.3, 0.1)
                        AgentFactory.getQLearner((SADomain) problemDomain, .99, 0.3, 0.1)
                );
            } else {
                attempt.visualize();
            }
        }
    }
}
