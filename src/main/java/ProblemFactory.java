import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.model.RewardFunction;

public class ProblemFactory {
    public static DomainGenerator gridWorld(){
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
        return gridWorldGenerator;
    }
}
