import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.domain.singleagent.cartpole.CartPoleDomain;
import burlap.domain.singleagent.cartpole.states.CartPoleFullState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.RewardFunction;

import java.util.function.Function;

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
    public void visualize() {
        super.visualize();
        EnvVisualize.cartPole((SADomain) createDomain(), initialState);
    }

    @Override
    public void performExperiment(Function<Domain, LearningAgentFactory> agentFactory) {
        super.performExperiment(agentFactory);
        Domain currentDomain = createDomain();
        Plotter.plot((SADomain)currentDomain, initialState, agentFactory.apply(currentDomain));
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
