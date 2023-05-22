package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import javafx.animation.SequentialTransition;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi.Constants.ONTOLOGY_BASE;

public class SituatedAgent extends AbstractDedaleAgent {

    AID agentSearched;
    OntModel model;
    OntDocumentManager dm;
    @Override
    protected void setup() {
        super.setup();
        List<Behaviour> lb = new ArrayList<>();
        lb.add(new OneShotBehaviour() {
            @Override
            public void action() {
                Agent agent = this.myAgent;
                DFAgentDescription dfd = new DFAgentDescription();
                dfd.setName(agent.getAID());
                ServiceDescription sd = new ServiceDescription();
                sd.setName("situated-agent");
                sd.setType("dedale");
                dfd.addServices(sd);
                try {
                    DFService.register(this.myAgent, dfd);
                    System.out.println("Situated agent registered!");
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        SequentialBehaviour seq = new SequentialBehaviour() {};
        seq.addSubBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {
                System.out.println("2. search");
                /*try {
                    Thread.sleep(2000);
                    agentSearched = searchAgent();
                } catch (FIPAException | InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
                try {
                    agentSearched = searchAgent();
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean done() {
                return agentSearched != null;
            }
        });
        seq.addSubBehaviour(new SimpleBehaviour() {
            boolean done = false;
            @Override
            public void action() {
                System.out.println("3. send");
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
                List obs = observe();
                ACLMessage informMess = new ACLMessage(ACLMessage.INFORM);
                informMess.setPerformative(ACLMessage.INFORM);
                try {
                    informMess.setContentObject((Serializable) obs);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                informMess.setSender(this.getAgent().getAID());
                informMess.addReceiver(agentSearched);
                send(informMess);
                done = true;
            }

            @Override
            public boolean done() {
                return done;
            }
        });
        MessageTemplate mt = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

        seq.addSubBehaviour(new AchieveREResponder(this, mt) {
            protected ACLMessage prepareResultNotification(ACLMessage requ, ACLMessage resp) {
                //OntClass nodeClass= model.getOntClass(ONTOLOGY_BASE+"#Nodo");

                String ind= requ.getContent();


                int index = ind.indexOf("#Nodo");

// Extract the remaining string from "#Nodo" onwards
                String remainingString = ind.substring(index + "#Nodo".length());
                gsLocation nodo= new gsLocation("");
                try {
                   // nodo= new gsLocation(requ.getContent());
                    nodo = new gsLocation(remainingString);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid location format. Please provide a valid digit.");
                    ACLMessage informDone = requ.createReply();
                    informDone.setPerformative(ACLMessage.REFUSE); //mensaje mal formateado
                }


                List<Couple<Location, List<Couple<Observation, Integer>>>> obs = observe();
                List<String> nodosContiguos = new ArrayList<>();
                for (Couple<Location, List<Couple<Observation, Integer>>> node : obs) {
                    boolean wind = false;
                    for (Couple<Observation, Integer> ob : node.getRight()) {
                        if (ob.getLeft() == Observation.WIND) {
                            System.out.println("WIND found");
                            wind = true;
                            break;
                        }
                    }
                    if (!wind) nodosContiguos.add(node.getLeft().toString());
                }
                String pos = getCurrentPosition().toString();
                ACLMessage informDone = requ.createReply();
                if (nodosContiguos.contains(nodo.toString())) {

                    /*ACLMessage agreeMessage = new ACLMessage(1);
                    send(agreeMessage);*/
                    //ENVIAR AGREE?

                    boolean moved= moveTo(nodo);
                    if (moved) {
                        informDone.setPerformative(ACLMessage.INFORM);

                        Object[] content = {pos, nodosContiguos};
                        try {
                            informDone.setContentObject(content);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        informDone.setPerformative(ACLMessage.FAILURE);
                    }
                }
                else{
                    informDone.setPerformative(ACLMessage.REFUSE);
                    Object[] content= {pos, nodo, nodosContiguos};
                    try {
                        informDone.setContentObject(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return informDone;
            }
        });

        lb.add(seq);

        addBehaviour(new startMyBehaviours(this, lb));
    }

    private AID searchAgent() throws FIPAException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        //templateSd.setName("deliberative-agent");
        templateSd.setType("bdi");
        template.addServices(templateSd);

        SearchConstraints sc = new SearchConstraints();
        // We want to receive 10 results at most
        sc.setMaxResults(new Long(10));

        DFAgentDescription[] results = DFService.search(this, template, sc);

        AID agenteBDI = null;
        if (results.length > 0) {
            DFAgentDescription dfd = results[0];
            agenteBDI = dfd.getName();
            System.out.println("The bdiAgent AID is " + agenteBDI);
        }
        return agenteBDI;
    }
}
