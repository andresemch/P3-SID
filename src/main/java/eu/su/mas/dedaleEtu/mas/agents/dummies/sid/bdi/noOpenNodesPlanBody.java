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
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.*;

public class noOpenNodesPlanBody extends BeliefGoalPlanBody  {

    private MapRepresentation map;

    private List<String> openNodes;

    private Set<String> closedNodes;

    private ArrayList hist;
    OntModel model;
    OntDocumentManager dm;

    @Override
    protected void execute() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        dm = model.getDocumentManager();
        dm.addAltEntry("ontologia", "file:./onto.owl");
        model.read("ontologia");
        if (openNodes!=null && openNodes.isEmpty()) {
            System.out.println("MAPA COMPLETADO");
            setEndState(Plan.EndState.SUCCESSFUL);
        }
       /*try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        BDIAgent2 agente = (BDIAgent2) this.myAgent;

        map = agente.getMap();
        openNodes = agente.getOpenNodes();
        closedNodes = agente.getClosedNodes();
        hist= agente.getHist();

        if (closedNodes != null) {
            ACLMessage inform=agente.receive();
            if (inform != null && inform.getPerformative() != 1) {
                hist.add(inform);
                if (inform.getPerformative() == 14){
                    Object[] m= new Object[0];
                    try {
                        m = (Object[]) inform.getContentObject();
                    } catch (UnreadableException e) {
                        throw new RuntimeException(e);
                    }

                    String position = m[0].toString();
                    String nodoRequested= m[1].toString();
                    List<String> nodosContiguos = (List<String>) m[2];

                    if (!closedNodes.contains(position)) {
                        openNodes.remove(position);
                        closedNodes.add(position);
                    }
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



                    List<String> path= map.getShortestPath(position,nodoRequested);

                    OntClass nodeClass= model.getOntClass(ONTOLOGY_BASE+"#Nodo");
                    Individual node = nodeClass.createIndividual(ONTOLOGY_BASE+"#"+"Nodo"+path.get(0));

                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                    request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
                    request.setContent(node.toString());
                    agente.send(request);
                    hist.add(request);

                }
                else {
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

                    if(!openNodes.isEmpty()) {
                        OntClass nodeClass= model.getOntClass(ONTOLOGY_BASE+"#Nodo");
                        Individual node = nodeClass.createIndividual(ONTOLOGY_BASE+"#"+"Nodo"+openNodes.get(0));
                        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                        request.addReceiver(new AID("Situated", AID.ISLOCALNAME));
                        request.setContent(node.toString());
                        agente.send(request);
                        hist.add(request);
                    }

                }
            }


        }

    }

}
