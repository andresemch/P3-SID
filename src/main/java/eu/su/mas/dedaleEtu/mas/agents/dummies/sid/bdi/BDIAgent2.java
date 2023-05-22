package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.TransientPredicate;
import bdi4jade.core.GoalUpdateSet;
import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.event.GoalEvent;
import bdi4jade.event.GoalListener;
import bdi4jade.goal.*;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;
import bdi4jade.reasoning.DefaultBeliefRevisionStrategy;
import bdi4jade.reasoning.DefaultDeliberationFunction;
import bdi4jade.reasoning.DefaultOptionGenerationFunction;
import bdi4jade.reasoning.DefaultPlanSelectionStrategy;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.MessageTemplate;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.*;

public class BDIAgent2 extends SingleCapabilityAgent {


    private static final String ONTOLOGY_BASE = "http://www.semanticweb.org/priyanka/ontologies/2023/3/untitled-ontology-17";

    private MapRepresentation mapa;

    private List<String> open_Nodes;

    private Set<String> closed_Nodes;
    public ArrayList goals= new ArrayList<>();

    public ArrayList hist= new ArrayList<>();

    public BDIAgent2(){
        // Create initial beliefs
        Belief iAmRegistered = new TransientPredicate(I_AM_REGISTERED, false);
        Belief ontology = new TransientBelief(ONTOLOGY, loadOntology());
        Belief openNodes= new TransientBelief(OPEN_NODES,open_Nodes);
        Belief map= new TransientBelief(MAPA,mapa);
        Belief closedNodes= new TransientBelief(CLOSED_NODES,closed_Nodes);
        Belief historical= new TransientBelief(HISTORICAL,hist);

        // Add initial desires

        Goal registerGoal = new PredicateGoal(I_AM_REGISTERED, true);
        Goal findSituatedGoal = new SPARQLGoal(ONTOLOGY, QUERY_SITUATED_AGENT);
        Goal receiveMessGoal = new PredicateGoal(RECEIVE_INITIAL_POS,true); //
        Goal noOpenNodesGoal = new PredicateGoal(NO_OPEN_NODES_LEFT, true);
        goals.add(registerGoal);
        goals.add(findSituatedGoal);
        goals.add(receiveMessGoal);
        goals.add(noOpenNodesGoal);
        SequentialGoal seqGoal= new SequentialGoal(goals);

        addGoal(seqGoal);

        // Declare goal templates
        GoalTemplate registerGoalTemplate = matchesGoal(registerGoal);
        GoalTemplate findSituatedTemplate = matchesGoal(findSituatedGoal);
        GoalTemplate receiveMessGoalTemplate = matchesGoal(receiveMessGoal); //
        GoalTemplate noOpenNodesTemplate = matchesGoal(noOpenNodesGoal);

        // Assign plan bodies to goals
        Plan registerPlan = new DefaultPlan(
                registerGoalTemplate, RegisterPlanBody.class);
        Plan findSituatedPlan = new DefaultPlan(
                findSituatedTemplate, FindSituatedPlanBody.class);
        Plan receiveMessPlan = new DefaultPlan(receiveMessGoalTemplate, receiveMessPlanBody.class); //

        Plan noOpenNodesPlan = new DefaultPlan(noOpenNodesTemplate, noOpenNodesPlanBody.class);


        // Init plan library
        getCapability().getPlanLibrary().addPlan(registerPlan);
        getCapability().getPlanLibrary().addPlan(findSituatedPlan);
        //getCapability().getPlanLibrary().addPlan(keepMailboxEmptyPlan);
        getCapability().getPlanLibrary().addPlan(receiveMessPlan); //
        getCapability().getPlanLibrary().addPlan(noOpenNodesPlan);

        // Init belief base
        getCapability().getBeliefBase().addBelief(iAmRegistered);
        getCapability().getBeliefBase().addBelief(ontology);
        getCapability().getBeliefBase().addBelief(openNodes);
        getCapability().getBeliefBase().addBelief(closedNodes);
        getCapability().getBeliefBase().addBelief(map);
        getCapability().getBeliefBase().addBelief(historical);

        // Add a goal listener to track events
        enableGoalMonitoring();

        // Override BDI cycle meta-functions, if needed
        overrideBeliefRevisionStrategy();
        overrideOptionGenerationFunction();
        overrideDeliberationFunction();
        overridePlanSelectionStrategy();


    }

    public MapRepresentation getMap() {
        return (MapRepresentation) getCapability().getBeliefBase().getBelief(MAPA).getValue();
    }
    public List<String> getOpenNodes() {
        //return this.open_Nodes;
        return (List<String>) getCapability().getBeliefBase().getBelief(OPEN_NODES).getValue();
    }

    public Set<String> getClosedNodes() {
        //return this.closed_Nodes;
        return (Set<String>) getCapability().getBeliefBase().getBelief(CLOSED_NODES).getValue();
    }

    public ArrayList getHist(){
        return (ArrayList) getCapability().getBeliefBase().getBelief(HISTORICAL).getValue();
    }

    public void setMap(MapRepresentation m) {
        //this.mapa = m;
        getCapability().getBeliefBase().updateBelief(MAPA,m);
    }
   public void setOpenNodes(List<String> on) {
        //this.open_Nodes = on;
       getCapability().getBeliefBase().updateBelief(OPEN_NODES,on);
    }

    public void setClosedNodes(Set<String> cn) {
        //this.closed_Nodes = cn;
        getCapability().getBeliefBase().updateBelief(CLOSED_NODES,cn);
    }

    public void setHist (ArrayList h){
        getCapability().getBeliefBase().updateBelief(HISTORICAL,h);
    }


    private void overrideBeliefRevisionStrategy() {
        this.getCapability().setBeliefRevisionStrategy(new DefaultBeliefRevisionStrategy() {
            @Override
            public void reviewBeliefs() {
                // This method should check belief base consistency,
                // make new inferences, etc.
                // The default implementation does nothing
            }
        });
    }

    private void overrideOptionGenerationFunction() {
        this.getCapability().setOptionGenerationFunction(new DefaultOptionGenerationFunction() {
            @Override
            public void generateGoals(GoalUpdateSet agentGoalUpdateSet) {
                // A GoalUpdateSet contains the goal status for the agent:
                // - Current goals (.getCurrentGoals)
                // - Generated goals, existing but not adopted yet (.getGeneratedGoals)
                // - Dropped goals, discarded forever (.getDroppedGoals)
                // This method should update these three sets (current,
                // generated, dropped).
                // The default implementation does nothing
            }
        });
    }

    private void overrideDeliberationFunction() {
        this.getCapability().setDeliberationFunction(new DefaultDeliberationFunction() {
            @Override
            public Set<Goal> filter(Set<GoalUpdateSet.GoalDescription> agentGoals) {
                // This method should choose which of the current goal
                // of the agent should become intentions in this iteration
                // of the BDI cycle.
                // The default implementation chooses all goals with no
                // actual filtering.
                return super.filter(agentGoals);
            }
        });
    }

    private void overridePlanSelectionStrategy() {
        this.getCapability().setPlanSelectionStrategy(new DefaultPlanSelectionStrategy() {
            @Override
            public Plan selectPlan(Goal goal, Set<Plan> capabilityPlans) {
                // This method should return a plan from a list of
                // valid (ordered) plans for fulfilling a particular goal.
                // The default implementation just chooses
                // the first plan of the list.
                return super.selectPlan(goal, capabilityPlans);
            }
        });
    }

    private void enableGoalMonitoring() {
        this.addGoalListener(new GoalListener() {
            @Override
            public void goalPerformed(GoalEvent goalEvent) {
                if(goalEvent.getStatus() == GoalStatus.ACHIEVED) {
                    System.out.println("BDI: " + goalEvent.getGoal() + " " +
                            "fulfilled!");
                }
            }
        });
    }

    private GoalTemplate matchesGoal(Goal goalToMatch) {
        return new GoalTemplate() {
            @Override
            public boolean match(Goal goal) {
                return goal == goalToMatch;
            }
        };
    }

    private Model loadOntology() {
        System.out.println("\n\n· Loading Ontology");
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("ontologia", "file:./onto.owl");
        model.read("ontologia");
        return model;
    }
}
