package ml_assn4;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import javafx.util.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


public abstract class ProblemAttempt {

    DomainGenerator domainGenerator;
    Boolean experimentSetup = false;

    public ProblemAttempt() {}

    protected void SetupExperiment(){
        this.experimentSetup = true;
        this.domainGenerator = createDomainGenerator();
    }

    public void performExperiment(List<BiFunction<Domain, State, Pair<ValueFunction, Policy>>> algAttempts){
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