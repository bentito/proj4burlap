import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.singleagent.oo.OOSADomain;

import java.util.function.Function;


public class Main {

    public static void main(String[] args) {
        boolean shouldPlot = false;

        if (args.length>0)
            shouldPlot = args[0].equals("plot");
        
        //throw a q-learner at the grid world problem
        GridWorldSolver solver = new GridWorldSolver((problemDomain) -> AgentFactory.getQLearner((OOSADomain) problemDomain, .99, 0.3, 0.1));

        if (shouldPlot){
            solver.generatePlots();
        } else {
            solver.visualize();
        }
    }
}
