package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import javafx.util.Pair;
import ml_assn4.problem_generation.Maze;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.BiFunction;

public class GridWorldSolver extends ProblemAttempt {

    int w;
    int h;

    int mazeWdth = 10;
    int mazeHeight = 10;

    class CustomGridWorldRewardFunction extends GridWorldRewardFunction {

        public CustomGridWorldRewardFunction(int width, int height, double initializingReward) {
            super(width, height, initializingReward);
        }

        @Override
        public double reward(State s, Action a, State sprime) {
            return super.reward(sprime, a, s);
        }
    }

    public GridWorldSolver() {
        super();
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
        initialState = new GridWorldState(
                new GridAgent(0, h-1)
        );
    }

    @Override
    public void visualizeProblem() {
        super.visualizeProblem();
        EnvVisualize.gridWorld((SADomain) createDomain(), ((GridWorldDomain)domainGenerator).getMap(), initialState);
    }

    @Override
    public void performExperiment(List<BiFunction<Domain, State, Pair<ValueFunction, Policy>>> algAttempts) {
        super.performExperiment(algAttempts);
        SADomain currentDomain = (SADomain) createDomain();

        for (BiFunction<Domain, State, Pair<ValueFunction, Policy>> algAttempt : algAttempts) {
            Pair<ValueFunction, Policy> p = algAttempt.apply(currentDomain, initialState);
            ml_assn4.EnvVisualize.gridWorldPolicy(currentDomain, initialState, p.getKey(), p.getValue(), w, h);
        }

//        ml_assn4.Plotter.plot(currentDomain, initialState, agentFactory.apply(currentDomain));
    }

    @Override
    DomainGenerator createDomainGenerator() {
        Maze m = new Maze(mazeWdth, mazeHeight);
        String mazeStr = m.toString();
        String[] mazeArrs = mazeStr.split("\n");
        mazeArrs = ArrayUtils.remove(mazeArrs, 0);
        mazeArrs = ArrayUtils.remove(mazeArrs, mazeArrs.length-1);

        w = mazeArrs[0].length()-2;
        h = mazeArrs.length;

        GridWorldDomain gridWorldGenerator = new GridWorldDomain(w, h); //11x11 grid world
        //stochastic transitions with 0.8 success rate
        gridWorldGenerator.setProbSucceedTransitionDynamics(0.8);

        for (int i = 0; i < mazeArrs.length; i++) {
            String rowString = mazeArrs[i].substring(1, mazeArrs[i].length()-1);
            for (int j = 0; j < rowString.length(); j++) {
                if(rowString.charAt(j) == '#'){
                    gridWorldGenerator.setCellWallState(i, j, 1);
                }
            }
        }

        GridWorldTerminalFunction tf = new GridWorldTerminalFunction();
        tf.markAsTerminalPosition(w-1, 0);

        GridWorldRewardFunction rf = new CustomGridWorldRewardFunction(w, h, -0.1);
        rf.setReward(w-1, 0, 15);

        gridWorldGenerator.setTf(tf);
        gridWorldGenerator.setRf(rf);
        return gridWorldGenerator;
    }
}
