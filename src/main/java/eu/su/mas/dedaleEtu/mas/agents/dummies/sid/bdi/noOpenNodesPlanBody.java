package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.*;

public class noOpenNodesPlanBody extends BeliefGoalPlanBody  {

    @Override
    protected void execute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BDIAgent2 agente = (BDIAgent2) this.myAgent;
        List<String> openNodes= (List<String>) agente.getCapability().getBeliefBase().getBelief(OPEN_NODES).getValue();
        Set<String> closedNodes = (Set<String>) agente.getCapability().getBeliefBase().getBelief(CLOSED_NODES).getValue();
        MapRepresentation map= (MapRepresentation) agente.getCapability().getBeliefBase().getBelief(MAPA).getValue();
        System.out.println("Open nodes in FIPA " + agente.getCapability().getBeliefBase().getBelief("Open Nodes").getValue());
        if (closedNodes != null) {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
            request.setContent(openNodes.get(0));
            agente.send(request);
            ACLMessage inform=agente.receive();
            System.out.println("Protocol finished. Rational Effect achieved.Received the following message:" +inform);

            if (openNodes.isEmpty()) {
                setEndState(Plan.EndState.SUCCESSFUL);
            }
        }
    }
}
