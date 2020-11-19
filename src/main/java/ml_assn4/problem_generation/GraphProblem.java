package ml_assn4.problem_generation;

import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import ml_assn4.Main;
import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GraphProblem {

    static String styleSheet =
            "node {" +
                "	fill-color: #2c2c2c;" +
                    "	z-index: 1;" +
                    "	text-mode: hidden;" +
            "}" +
            "edge {" +
                "	fill-color: #A9A9A9;" +
                    "	z-index: 0;" +
            "}" +
            "node.marked {" +
                "	fill-color: red;" +
                    "	z-index: 2;" +
            "}" +
            "edge.marked {" +
                "	fill-color: red;" +
                "	size: 2;" +
                "	z-index: 2;" +
            "}" +
            "node.goal {" +
                "	fill-color: green;" +
                    "	z-index: 2;" +
            "}" +
            "node.start {" +
                "	fill-color: blue;" +
                    "	z-index: 2;" +
            "}";

    public static class GraphProblemGenerator {
        String getGraphName(){ return "";}
        Generator getGraphGenerator(){ return null;}
    }

    public static GraphProblemGenerator getDoroGraphGenerator(){
        return new GraphProblemGenerator(){
            @Override
            String getGraphName() {
                return  "Dorogovtsev mendes";
            }

            @Override
            Generator getGraphGenerator() {
                Random rand = new Random(Main.SEED);
                return new DorogovtsevMendesGenerator(rand);
            }
        };
    }

    public static GraphProblemGenerator getRandEuclideanGraphGenerator(){
        return new GraphProblemGenerator(){
            @Override
            String getGraphName() {
                return  "random euclidean";
            }

            @Override
            Generator getGraphGenerator() {
                RandomEuclideanGenerator gen = new RandomEuclideanGenerator();
                gen.setRandomSeed(Main.SEED);
                return gen;
            }
        };
    }

    public static GraphProblemGenerator getRandGraphGenerator(){
        return new GraphProblemGenerator(){
            @Override
            String getGraphName() {
                return  "random";
            }

            @Override
            Generator getGraphGenerator() {
                return new RandomGenerator(Main.SEED, true, true);
            }
        };
    }

    public static GraphProblemGenerator getGridraphGenerator(){
        return new GraphProblemGenerator(){
            @Override
            String getGraphName() {
                return  "grid";
            }

            @Override
            Generator getGraphGenerator() {
                return new GridGenerator(false, true, true, true);
            }
        };
    }

    public static Graph generateGraph(GraphProblemGenerator graphGenerator, int nodes){
        return generateGraph(graphGenerator, nodes, false);
    }

    public static Graph generateGraph(GraphProblemGenerator graphGenerator, int nodes, Boolean removeRandomEdges){
        Graph graph = new SingleGraph(graphGenerator.getGraphName());
        Generator gen = graphGenerator.getGraphGenerator();
        gen.addSink(graph);
        gen.begin();
        for(int i=0; i<nodes; i++) {
            gen.nextEvents();
        }
        gen.end();

        if(removeRandomEdges){
            Random rand = new Random(Main.SEED);
            List<Edge> removeEdges = graph.edges().filter(edge -> rand.nextDouble() > 0.8).collect(Collectors.toList());

            List<Edge> removeEdges2 = removeEdges.stream().filter(edge ->
                    edge.getNode0().neighborNodes().count() > 1 && edge.getNode1().neighborNodes().count() > 1
            ).collect(Collectors.toList());

            removeEdges2.forEach(graph::removeEdge);
        }

        return graph;
    }

    public static GraphDefinedDomain graphToDomainGenerator(Graph g){
        GraphDefinedDomain graphDomain = new GraphDefinedDomain();

        g.nodes().forEach(currNode -> {
            currNode.setAttribute("ui.label", currNode.getId());
            currNode.edges().forEach(currEdge -> {
                Node op = currEdge.getOpposite(currNode);

                List<Node> neighbors = currNode.neighborNodes().collect(Collectors.toList());

                double numLowProb = (double) neighbors.size()-1;
                double probSucceed = .8;
                double pAlt = (1.-probSucceed)/numLowProb;

                double totalP = 0.;

                for (Node neighborNode : neighbors) {
                    // srcNode action destNode p
                    StringBuilder builder = new StringBuilder();
                    builder.append(currNode.getId());
                    builder.append(" ");
                    builder.append(currEdge.getIndex());
                    builder.append(" ");
                    builder.append(neighborNode.getId());
                    builder.append(" ");
                    double localP;
                    if(neighborNode.equals(op)){
                        localP = probSucceed;
                    }
                    else
                        localP = pAlt;

                    builder.append(localP);
                    totalP += localP;
//                    System.out.println(builder.toString());

                    graphDomain.setTransition(
                            Integer.parseInt(currNode.getId()),
                            currEdge.getIndex(),
                            Integer.parseInt(neighborNode.getId()),
                            localP
                    );
                }
//                System.out.println(currNode.getId() + " total prob [" + totalP + "] " + (totalP == 1.) + "\n");
            });

        });

        return graphDomain;
    }

    public static void iterStep(Graph g, String startNode) {

        g.setAttribute("ui.stylesheet", styleSheet);

        g.display(true);

        // explore
        Iterator<? extends Node> k = g.getNode(startNode).getDepthFirstIterator();

        Node prev = k.next();
        prev.setAttribute("ui.class", "marked");
        try { Thread.sleep(1000); } catch (Exception ignored) {}

        while (k.hasNext()) {
            prev.removeAttribute("ui.class");
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            try { Thread.sleep(1000); } catch (Exception ignored) {}
            prev = next;
        }
    }

    public static void visualizePolicy(Graph g, List<String> visitedNodes, String startNode, String goalNode) {
        g.setAttribute("ui.stylesheet", styleSheet);
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

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

//        GraphProblemGenerator graphGen = GraphProblem.getDoroGraphGenerator();
//        GraphProblemGenerator graphGen = GraphProblem.getRandGraphGenerator();
//        GraphProblemGenerator graphGen = GraphProblem.getGridraphGenerator();
        GraphProblemGenerator graphGen = GraphProblem.getRandEuclideanGraphGenerator();

        Graph graph = GraphProblem.generateGraph(graphGen, 500);

        GraphDefinedDomain graphDomain = GraphProblem.graphToDomainGenerator(graph);

        System.out.println(graphDomain.invalidMDPReport());
        System.out.println("valid: " + graphDomain.isValidMDPGraph());

        iterStep(graph, "0");
    }

}
