package ml_assn4;

import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;

import java.util.function.Function;


public abstract class ProblemAttempt {

    DomainGenerator domainGenerator;
    Boolean experimentSetup = false;

    public ProblemAttempt() {}

    protected void SetupExperiment(){
        this.experimentSetup = true;
        this.domainGenerator = createDomainGenerator();
    }

    public void performExperiment(Function<Domain, LearningAgentFactory> agentFactory){
        if(!this.experimentSetup){
            SetupExperiment();
        }
    }

    public void visualizeProblem(){
        if(!this.experimentSetup){
            SetupExperiment();
        }
    }

    protected Domain createDomain(){
        return domainGenerator.generateDomain();
    }

    abstract DomainGenerator createDomainGenerator();
}