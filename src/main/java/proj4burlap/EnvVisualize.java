package proj4burlap;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import proj4burlap.problem_generation.GraphProblem;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Iterator;
import java.util.List;

public class EnvVisualize {

    public static void gridWorld(SADomain domain, int[][] map, State initialState){
        //create visualizer and explorer
        Visualizer v = GridWorldVisualizer.getVisualizer(map);
        VisualExplorer exp = new VisualExplorer(domain, v, initialState);

        //set control keys to use w-s-a-d
        exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
        exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
        exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");
        exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");

        exp.initGUI();
    }

    public static void gridWorldPolicy(SADomain domain, State initialState, ValueFunction valueFunction, Policy p, int width, int height, String algName){
        SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();
        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(allStates, width, height, valueFunction, p);
        gui.initGUI();
        gui.setTitle(algName);
    }

    public static void graphPolicy(Graph g, List<String> visitedNodes, String startNode, String goalNode) {
        g.setAttribute("ui.stylesheet", GraphProblem.styleSheet);
        g.display(true);

        Iterator<String> visitedIterator = visitedNodes.iterator();

        String prev = visitedIterator.next();

        while (visitedIterator.hasNext()) {
            Node prevN = g.getNode(prev);
            prevN.setAttribute("ui.class", "marked");

            String next = visitedIterator.next();
            Edge currEdge = g.getEdge(String.format("%s-%s", prev, next));
            if(currEdge == null){
                currEdge = g.getEdge(String.format("%s-%s", next, prev));
            }

            currEdge.setAttribute("ui.class", "marked");

            prev = next;
        }
        // color start/finish at end to ensure proper color
        Node start = g.getNode(startNode);
        Node goal = g.getNode(goalNode);
        start.setAttribute("ui.class", "start");
        goal.setAttribute("ui.class", "goal");
    }
}
