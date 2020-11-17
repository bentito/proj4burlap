package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.cartpole.InvertedPendulum;
import burlap.domain.singleagent.cartpole.states.InvertedPendulumState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import javafx.util.Pair;

import java.util.List;
import java.util.function.BiFunction;

public class CartPoleSolver extends ProblemAttempt {

    public CartPoleSolver() {
        super();
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
//        initialState = new CartPoleFullState();
        initialState = new InvertedPendulumState(Math.PI/6, 1.);
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
//        CartPoleDomain cartPoleGenerator = new CartPoleDomain();
        InvertedPendulum gen = new InvertedPendulum();
        gen.physParams.actionNoise = 0.;
        gen.physParams.angleRange = Math.PI/4;

//        cartPoleGenerator.setToCorrectModel();

        //ends when the agent reaches a location
//        TerminalFunction tf = new CartPoleDomain.CartPoleTerminalFunction();
        TerminalFunction tf = new InvertedPendulum.InvertedPendulumTerminalFunction(Math.PI/4);

        //reward function definition
//        RewardFunction rf = new CartPoleDomain.CartPoleRewardFunction();
        RewardFunction rf = new testRF(Math.PI/4, -.1, 50);

        gen.setTf(tf);
        gen.setRf(rf);
        return gen;
    }
}

class testRF implements RewardFunction{
    /**
     * The maximum pole angle to cause termination/failure.
     */
    double maxAbsoluteAngle = Math.PI / 2.;
    double defaultReward = 0;
    double peakReward = 1;

    public testRF() {

    }

    /**
     * Initializes with a max pole angle as specified in radians
     * @param maxAbsoluteAngle the maximum pole angle in radians that causes task termination/failure.
     */
    public testRF(double maxAbsoluteAngle, double defaultReward, double peakReward){
        this.maxAbsoluteAngle = maxAbsoluteAngle;
        this.defaultReward = defaultReward;
        this.peakReward = peakReward;
    }


    @Override
    public double reward(State s, Action a, State sprime) {

        double failReward = -1;

        InvertedPendulumState is = (InvertedPendulumState)sprime;
        double ang = is.angle;
        System.out.println(ang);

        if(Math.abs(ang) >= maxAbsoluteAngle){
            return failReward;
        } else if (Math.abs(ang) == 0){
            return peakReward;
        }

        return defaultReward;
    }
}
