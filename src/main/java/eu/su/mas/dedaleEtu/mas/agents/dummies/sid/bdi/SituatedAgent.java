package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

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
import jade.proto.AchieveREResponder;
import javafx.animation.SequentialTransition;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SituatedAgent extends AbstractDedaleAgent {

    AID agentSearched;
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
                try {
                    Thread.sleep(2000);
                    agentSearched = searchAgent();
                } catch (FIPAException | InterruptedException e) {
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
                //gsLocation l=  new gsLocation(requ.getContent());
               // moveTo(l);
                //requ=receive();
                System.out.println("Responder has received the following message:"+requ);
                gsLocation nodo= new gsLocation(requ.getContent());
                moveTo(nodo);
                ACLMessage informDone = requ.createReply();
                informDone.setPerformative(ACLMessage.INFORM);
                if(Objects.equals(getCurrentPosition().toString(), nodo.toString())){
                    informDone.setContent("inform done, me he movido");
                }
                else informDone.setContent("inform not done, no me he movido");
                return informDone;
            }
        });

        /*lb.add(new CyclicBehaviour() {
            boolean finishedSearch = false;
            @Override
            public void action() {
                while (!finishedSearch) {
                    try {
                        agentSearched = searchAgent();
                        if (agentSearched != null) finishedSearch = true;
                    } catch (FIPAException e) {
                        throw new RuntimeException(e);
                    }
                    if (finishedSearch) {
                        System.out.println("Situated ha encontrado a bdi");
                        System.out.println(agentSearched);
                    }
                }
            }

        });*/

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
