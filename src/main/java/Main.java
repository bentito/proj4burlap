import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;


public class Main {

    public static void main(String[] args) {
        boolean shouldPlot = false;

        if (args.length>0){
            shouldPlot = args[0].equals("plot");
        }

        GridWorldDomain gridWorldGenerator = new GridWorldDomain(11,11); //11x11 grid world
        gridWorldGenerator.setMapToFourRooms(); //four rooms layout
        gridWorldGenerator.setProbSucceedTransitionDynamics(0.8); //stochastic transitions with 0.8 success rate

        //ends when the agent reaches a location
        final TerminalFunction tf = new SinglePFTF(
                PropositionalFunction.findPF(gridWorldGenerator.generatePfs(), GridWorldDomain.PF_AT_LOCATION));

        //reward function definition
        final RewardFunction rf = new GoalBasedRF(new TFGoalCondition(tf), 5., -0.1);

        gridWorldGenerator.setTf(tf);
        gridWorldGenerator.setRf(rf);

        final OOSADomain domain = gridWorldGenerator.generateDomain(); //generate the grid world domain

        //setup initial state
        State s = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));

        if (shouldPlot){
            LearningAgentFactory qLearningFactory = AgentFactory.getQLearner(domain, .99, 0.3, 0.1);
            Plotter.plot(domain, s, qLearningFactory);
        } else {
            EnvVisualize.gridWorld(domain, gridWorldGenerator.getMap(), s);
        }
    }

}
