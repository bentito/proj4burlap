package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.cartpole.InvertedPendulum;
import burlap.domain.singleagent.cartpole.states.InvertedPendulumState;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import javafx.util.Pair;
import ml_assn4.problem_generation.GraphProblem;
import org.graphstream.graph.Graph;

import java.util.List;
import java.util.function.BiFunction;

public class GraphProblemSolver extends ProblemAttempt {

    public GraphProblemSolver() {
        super();
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
        initialState = new GraphStateNode(0);
    }

    @Override
    public void visualizeProblem() {
        super.visualizeProblem();
//        EnvVisualize.cartPole((SADomain) createDomain(), initialState);
    }

    public void performExperiment(List<BiFunction<Domain, State, Pair<ValueFunction, Policy>>> algAttempts) {
        super.performExperiment(algAttempts);
        Domain currentDomain = createDomain();

        for (BiFunction<Domain, State, Pair<ValueFunction, Policy>> algAttempt : algAttempts) {
            Pair<ValueFunction, Policy> p = algAttempt.apply(currentDomain, initialState);
            //do something with alg results
        }

//        Plotter.plot((SADomain)currentDomain, initialState, agentFactory.apply(currentDomain));
    }

    @Override
    DomainGenerator createDomainGenerator() {
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getDoroGraphGenerator();
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getRandGraphGenerator();
        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getRandEuclideanGraphGenerator();
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getGridraphGenerator();
//        test graphGen = GraphProblem.getRandGraphGenerator();

        Graph graph = GraphProblem.generateGraph(graphGen, 800);
        GraphDefinedDomain graphDomain = GraphProblem.graphToDomainGenerator(graph);

        int goalState = 768;

        //ends when the agent reaches a location
        TerminalFunction tf = new testTF(goalState);

        //reward function definition
        RewardFunction rf = new testRF( goalState,500., -0.1);

        graphDomain.setTf(tf);
        graphDomain.setRf(rf);
        return graphDomain;
    }
}

class testTF implements TerminalFunction{

    int goalStateId;

    public testTF(int goalStateId) {
        this.goalStateId = goalStateId;
    }

    @Override
    public boolean isTerminal(State s) {
        GraphStateNode currentNodeState = ((GraphStateNode)s);
        int sid = currentNodeState.getId();
        return sid == goalStateId;
    }
}

class testRF implements RewardFunction{

    double defaultReward;
    double goalReward;
    int goalStateId;

    public testRF(int goalStateId, double goalReward, double defaultReward){
        this.defaultReward = defaultReward;
        this.goalReward = goalReward;
        this.goalStateId = goalStateId;
    }

    @Override
    public double reward(State s, Action a, State sprime) {

        GraphStateNode currentNodeState = ((GraphStateNode)s);
        int sid = currentNodeState.getId();

        if(sid == goalStateId)
            return goalReward;

        return defaultReward;
    }
}
