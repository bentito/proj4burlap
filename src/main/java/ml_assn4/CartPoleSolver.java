package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.cartpole.CartPoleDomain;
import burlap.domain.singleagent.cartpole.states.CartPoleFullState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.RewardFunction;
import javafx.util.Pair;

import java.util.List;
import java.util.function.BiFunction;

public class CartPoleSolver extends ProblemAttempt {

    State initialState;

    public CartPoleSolver() {
        super();
    }

    @Override
    protected void SetupExperiment() {
        super.SetupExperiment();
        initialState = new CartPoleFullState();
    }

    @Override
    public void visualizeProblem() {
        super.visualizeProblem();
        EnvVisualize.cartPole((SADomain) createDomain(), initialState);
    }

    @Override
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
        CartPoleDomain cartPoleGenerator = new CartPoleDomain();
        cartPoleGenerator.setToCorrectModel();

        //ends when the agent reaches a location
        TerminalFunction tf = new CartPoleDomain.CartPoleTerminalFunction();

        //reward function definition
        RewardFunction rf = new CartPoleDomain.CartPoleRewardFunction();

        cartPoleGenerator.setTf(tf);
        cartPoleGenerator.setRf(rf);
        return cartPoleGenerator;
    }
}
