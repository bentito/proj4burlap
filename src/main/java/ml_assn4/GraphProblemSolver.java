package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.auxiliary.common.ConstantStateGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import javafx.util.Pair;
import ml_assn4.problem_generation.GraphProblem;
import org.graphstream.graph.Graph;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GraphProblemSolver extends ProblemAttempt {

    Graph currentGraph = null;
    int goalState = 776;
    int startState = 1435;
    int numNodes;

    public GraphProblemSolver(int numNodes) {
        super();
        this.numNodes = numNodes;
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
        initialState = new GraphStateNode(startState);
    }

    @Override
    public void visualizeProblem() {
        super.visualizeProblem();
//        EnvVisualize.cartPole((SADomain) createDomain(), initialState);
    }

    public void performExperiment(List<AlgExperiment>  algAttempts) {
        super.performExperiment(algAttempts);
        SADomain currentDomain = (SADomain)createDomain();

        for (AlgExperiment algAttempt : algAttempts) {
            this.startMeasureTime();
            Pair<ValueFunction, Policy> p = algAttempt.performExperiment(currentDomain, initialState);
            this.finishMeasureTime(algAttempt.getAlgName());

            ConstantStateGenerator sg = new ConstantStateGenerator(initialState);
            Episode thisEp = PolicyUtils.rollout(p.getValue(), new SimulatedEnvironment(currentDomain, sg), 5000);

            List<String> visitedNodes = thisEp.stateSequence.stream().map(Objects::toString).collect(Collectors.toList());

            EnvVisualize.graphPolicy(this.currentGraph, visitedNodes, Integer.toString(startState), Integer.toString(goalState));
        }
    }

    @Override
    DomainGenerator createDomainGenerator() {
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getRandEuclideanGraphGenerator();
        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getDoroGraphGenerator();
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getRandGraphGenerator();
//        GraphProblem.GraphProblemGenerator graphGen = GraphProblem.getGridraphGenerator();
//        test graphGen = GraphProblem.getRandGraphGenerator();

        this.currentGraph = GraphProblem.generateGraph(graphGen, this.numNodes, false);
        GraphDefinedDomain graphDomain = GraphProblem.graphToDomainGenerator(this.currentGraph);

        //ends when the agent reaches a location
        TerminalFunction tf = s -> {
            GraphStateNode currentNodeState = ((GraphStateNode)s);
            int sid = currentNodeState.getId();
            return sid == goalState;
        };

        //reward function definition
        RewardFunction rf = ((s, a, sprime) -> {
            GraphStateNode currentNodeState = ((GraphStateNode)sprime);
            int sid = currentNodeState.getId();

            if(sid == goalState)
                return 5;

            return -0.1;
        });

        graphDomain.setTf(tf);
        graphDomain.setRf(rf);
        return graphDomain;
    }
}
