package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.*;

public class receiveMessPlanBody extends BeliefGoalPlanBody {

    private MapRepresentation map;
    /**
     * Nodes known but not yet visited
     */
    private List<String> openNodes;
    /**
     * Visited nodes
     */
    private Set<String> closedNodes;


    @Override
    protected void execute() {
        BDIAgent2 agente = (BDIAgent2) this.myAgent;
        ACLMessage msg = agente.receive();
        if (msg != null) {
            System.out.println("The message receive is " + msg.getContent());

            this.map = new MapRepresentation();
            this.openNodes = new ArrayList<>();
            this.closedNodes = new HashSet<>();

            Object m= new Object();
            try {
                m = (Object) msg.getContentObject();
            } catch (UnreadableException e) {
                throw new RuntimeException(e);
            }
            List<Couple<Location, List<Couple<Observation, Integer>>>> lobs = (List<Couple<Location, List<Couple<Observation, Integer>>>>) m;
            String myPosition = String.valueOf(lobs.get(0).getLeft());
            lobs.remove(0);

            String nextNode = null;
            this.map.addNode(myPosition, MapRepresentation.MapAttribute.closed);
            for (Couple<Location, List<Couple<Observation, Integer>>> lob : lobs) {
                String nodeId = String.valueOf(lob.getLeft());
                if (!this.closedNodes.contains(nodeId)) {
                    if (!this.openNodes.contains(nodeId)) {
                        this.openNodes.add(nodeId);
                        this.map.addNode(nodeId, MapRepresentation.MapAttribute.open);
                        this.map.addEdge(myPosition, nodeId);
                    } else {
                        //the node exist, but not necessarily the edge
                        this.map.addEdge(myPosition, nodeId);
                    }
                    if (nextNode == null) nextNode = nodeId;
                }
            }
            this.closedNodes.add(myPosition);

            System.out.println("open nodes" + openNodes);
            System.out.println("closed nodes" + closedNodes);
            System.out.println("map " + map);

            agente.setMap(map);
            //agente.getCapability().getBeliefBase().updateBelief(MAPA,map);
            agente.setOpenNodes(openNodes);
            //agente.getCapability().getBeliefBase().updateBelief(OPEN_NODES,openNodes);
            agente.setClosedNodes(closedNodes);
            //agente.getCapability().getBeliefBase().updateBelief(CLOSED_NODES,closedNodes);

            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
            request.setContent(openNodes.get(0));
            agente.send(request);

            setEndState(Plan.EndState.SUCCESSFUL);
        }
    }

}
