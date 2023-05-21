package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class noOpenNodesPlanBody extends BeliefGoalPlanBody  {

    private MapRepresentation map;

    private List<String> openNodes;

    private Set<String> closedNodes;

    @Override
    protected void execute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BDIAgent2 agente = (BDIAgent2) this.myAgent;
        //System.out.println("Open nodes in FIPA " + agente.getCapability().getBeliefBase().getBelief("Open Nodes").getValue());

        map = agente.getMap();
        openNodes = agente.getOpenNodes();
        //closedNodes = agente.getClosedNodes();

        if (agente.getClosedNodes() != null) {
            //List<String> openNodes= (List<String>) agente.getCapability().getBeliefBase().getBelief("Open Nodes").getValue();

            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
            request.setContent(openNodes.get(0));
            agente.send(request);

            //openNodes.remove(0);

            ACLMessage inform=agente.receive();
            if (inform != null) {
                System.out.println("Protocol finished. Rational Effect achieved.Received the following message:" + inform);
                Object[] m = new Object[0];
                try {
                    m = (Object[]) inform.getContentObject();
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }

                String position = m[0].toString();
                List<String> nodosContiguos = (List<String>) m[1];

                for (String node : nodosContiguos) {
                    //if (!openNodes.contains(position) && !closedNodes.contains(node)) openNodes.add(node);


                }

                System.out.println("Position: " + position);
                System.out.println("Nodos contiguos: " + nodosContiguos);

                System.out.println("Open nodes: " + openNodes);
                //System.out.println("Closed nodes: " + closedNodes);
            }

            if (((List<String>) agente.getCapability().getBeliefBase().getBelief("Open Nodes").getValue()).isEmpty()) {
                setEndState(Plan.EndState.SUCCESSFUL);
            }
        }
    }
}
