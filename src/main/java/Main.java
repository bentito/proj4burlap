import burlap.mdp.singleagent.oo.OOSADomain;


public class Main {

    public static void main(String[] args) {
        boolean shouldPlot = false;

        if (args.length>0)
            shouldPlot = args[0].equals("plot");
        
        GridWorldSolver solver = new GridWorldSolver();

        if (shouldPlot){
            //throw a q-learner at the grid world problem
            solver.performExperiment((problemDomain) ->
                    AgentFactory.getQLearner((OOSADomain) problemDomain, .99, 0.3, 0.1)
            );
        } else {
            solver.visualize();
        }
    }
}
