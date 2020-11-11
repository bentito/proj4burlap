import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.model.RewardFunction;

import java.util.function.Function;

public class GridWorldSolver extends ProblemAttempt {

    State initialState;

    public GridWorldSolver() {
        super();
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));
    }

    @Override
    Domain createDomain() {
        return domainGenerator.generateDomain();
    }

    @Override
    void visualize() {
        EnvVisualize.gridWorld((SADomain) createDomain(), ((GridWorldDomain)domainGenerator).getMap(), initialState);
    }

    @Override
    public void performExperiment(Function<Domain, LearningAgentFactory> agentFactory) {
        super.performExperiment(agentFactory);
        Domain currentDomain = createDomain();
        Plotter.plot((SADomain)currentDomain, initialState, agentFactory.apply(currentDomain));
    }

    @Override
    DomainGenerator createDomainGenerator() {
        GridWorldDomain gridWorldGenerator = new GridWorldDomain(11,11); //11x11 grid world
        gridWorldGenerator.setMapToFourRooms();
        //stochastic transitions with 0.8 success rate
        gridWorldGenerator.setProbSucceedTransitionDynamics(0.8);

        //ends when the agent reaches a location
        final TerminalFunction tf = new SinglePFTF(
                PropositionalFunction.findPF(
                        gridWorldGenerator.generatePfs(),
                        GridWorldDomain.PF_AT_LOCATION
                )
        );

        //reward function definition
        final RewardFunction rf = new GoalBasedRF(new TFGoalCondition(tf), 5., -0.1);

        gridWorldGenerator.setTf(tf);
        gridWorldGenerator.setRf(rf);
        return gridWorldGenerator;
    }
}
