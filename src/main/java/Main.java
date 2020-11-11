import burlap.mdp.singleagent.oo.OOSADomain;


public class Main {

    public static void main(String[] args) {
        boolean shouldExperiment = false;

        if (args.length>0)
            shouldExperiment = args[0].equals("run");
        
        GridWorldSolver solver = new GridWorldSolver();

        if (shouldExperiment){
            //throw a q-learner at the grid world problem
            solver.performExperiment((problemDomain) ->
                    AgentFactory.getQLearner((OOSADomain) problemDomain, .99, 0.3, 0.1)
            );
        } else {
            solver.visualize();
        }
    }
}
