import burlap.domain.singleagent.cartpole.CartPoleDomain;
import burlap.domain.singleagent.cartpole.CartPoleVisualizer;
import burlap.domain.singleagent.cartpole.states.CartPoleFullState;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

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

    public static void cartPole(SADomain domain, State initialState){
        VisualExplorer exp = new VisualExplorer(domain, CartPoleVisualizer.getCartPoleVisualizer(), initialState);
        exp.addKeyAction("a", CartPoleDomain.ACTION_LEFT, "");
        exp.addKeyAction("d", CartPoleDomain.ACTION_RIGHT, "");

        exp.initGUI();
    }
}
