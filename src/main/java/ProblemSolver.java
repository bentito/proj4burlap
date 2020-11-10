import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;

import java.util.function.Function;


public abstract class ProblemSolver{

    LearningAgentFactory agentFactory;
    DomainGenerator domainGenerator;
    Domain currentDomain;

    public ProblemSolver(Function<Domain, LearningAgentFactory> agentFactory) {
        this.domainGenerator = createDomainGenerator();
        this.currentDomain = createDomain();
        this.agentFactory = agentFactory.apply(this.currentDomain);
    }

    abstract void visualize();
    abstract void generatePlots();
    abstract Domain createDomain();
    abstract DomainGenerator createDomainGenerator();
}