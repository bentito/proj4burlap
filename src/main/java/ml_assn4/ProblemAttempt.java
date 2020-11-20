package ml_assn4;

import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;

import java.util.List;
import java.util.function.BiFunction;


public abstract class ProblemAttempt {

    State initialState;
    DomainGenerator domainGenerator;
    Boolean experimentSetup = false;

    long startTime = 0;

    public ProblemAttempt() {}

    protected void SetupExperiment(){
        this.experimentSetup = true;
        this.domainGenerator = createDomainGenerator();
    }

    public void performExperiment(List<AlgExperiment> algAttempts){
        if(!this.experimentSetup){
            SetupExperiment();
        }
    }

    protected void startMeasureTime(){
        startTime = System.nanoTime();
    }

    protected void finishMeasureTime(String algName){
        System.out.printf("%s Experiment Duration [%d]\n", algName, System.nanoTime() - startTime);
    }

    @SafeVarargs
    public final void createLearningPlots(int trialLength, BiFunction<Domain, State, LearningAgentFactory>... agentFactoryCreators){
        this.SetupExperiment();
        SADomain currentDomain = (SADomain) createDomain();

        LearningAgentFactory[] f = new LearningAgentFactory[agentFactoryCreators.length];
        for (int i = 0; i < agentFactoryCreators.length; i++) {
            f[i] = agentFactoryCreators[i].apply(currentDomain, initialState);
        }

        Plotter.plot(currentDomain, initialState, trialLength, f);
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