package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.*;

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
        boolean refused=false;
        //List<String> openNodes= (List<String>) agente.getCapability().getBeliefBase().getBelief(OPEN_NODES).getValue();
        //Set<String> closedNodes = (Set<String>) agente.getCapability().getBeliefBase().getBelief(CLOSED_NODES).getValue();
        //MapRepresentation map= (MapRepresentation) agente.getCapability().getBeliefBase().getBelief(MAPA).getValue();
        //System.out.println("Open nodes in FIPA " + agente.getCapability().getBeliefBase().getBelief("Open Nodes").getValue());

        map = agente.getMap();
        openNodes = agente.getOpenNodes();
        closedNodes = agente.getClosedNodes();

        if (closedNodes != null) {
            //System.out.println("ENTRA AQUÍ");
            if(!refused) {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
                request.setContent(openNodes.get(0));
                agente.send(request);
            }
            ACLMessage inform=agente.receive();
            if (inform != null) {
                //TENDRÍA QUE SER UNA REPLANIFICACIÓN???????????
                if (inform.getPerformative() == 14){
                    refused=true;
                    System.out.println("REFUSED");
                    Object[] m= new Object[0];
                    try {
                        m = (Object[]) inform.getContentObject();
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }

                    String position = m[0].toString();
                    String nodoRequested= m[1].toString();

                    if (!closedNodes.contains(position)) {
                        openNodes.remove(position);
                        closedNodes.add(position);
                    }

                    List<String> path= map.getShortestPath(position,nodoRequested);
                    System.out.println("THE PATH IS: "+path+"\n");

                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                    request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
                    request.setContent(path.get(0));
                    agente.send(request);

                }
                else {
                    //System.out.println("Protocol finished. Rational Effect achieved.Received the following message:" + inform);
                    Object[] m = new Object[0];
                    try {
                        m = (Object[]) inform.getContentObject();
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }

                    String position = m[0].toString();
                    List<String> nodosContiguos = (List<String>) m[1];
                    if (!closedNodes.contains(position)) {
                        openNodes.remove(position);
                        closedNodes.add(position);
                    }

                    //SE PODRÍA MOVER DE VECINO EN VECINO Y LUEGO A LOS OPEN NODES??????????
                    nodosContiguos.remove(0);
                    String nextNode = null;
                    this.map.addNode(position, MapRepresentation.MapAttribute.closed);
                    for (String nodeId : nodosContiguos) {
                        if (!this.closedNodes.contains(nodeId)) {
                            if (!this.openNodes.contains(nodeId)) {
                                this.openNodes.add(nodeId);
                                this.map.addNode(nodeId, MapRepresentation.MapAttribute.open);
                                this.map.addEdge(position, nodeId);
                            } else {
                                //the node exist, but not necessarily the edge
                                this.map.addEdge(position, nodeId);
                            }
                            if (nextNode == null) nextNode = nodeId;
                        }
                    }
                    this.closedNodes.add(position);

                    System.out.println("Position: " + position);
                    System.out.println("Nodos contiguos: " + nodosContiguos);
                    System.out.println("Open nodes: " + openNodes);
                    System.out.println("Closed nodes: " + closedNodes);
                }
            }

            if (openNodes.isEmpty()) {
                System.out.println("MAPA COMPLETADO");
                setEndState(Plan.EndState.SUCCESSFUL);
            }
        }
    }

}
