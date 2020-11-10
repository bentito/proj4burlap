import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;

import java.util.function.Function;

public class GridWorldSolver extends ProblemSolver{

    State initialState;

    public GridWorldSolver(Function<Domain, LearningAgentFactory> agentFactory) {
        super(agentFactory);
        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));
    }

    void visualize() {
        EnvVisualize.gridWorld((SADomain) currentDomain, ((GridWorldDomain)domainGenerator).getMap(), initialState);
    }

    void generatePlots() {
        Plotter.plot((SADomain)currentDomain, initialState, agentFactory);
    }

    @Override
    Domain createDomain() {
        return domainGenerator.generateDomain();
    }

    @Override
    DomainGenerator createDomainGenerator() {
        return ProblemFactory.gridWorld();
    }
}
