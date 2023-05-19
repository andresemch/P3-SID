package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

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
import javafx.animation.SequentialTransition;

import java.util.ArrayList;
import java.util.List;

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
            @Override
            public void action() {
                List obs = observe();
                ACLMessage informMess = new ACLMessage(ACLMessage.INFORM);
                informMess.setPerformative(ACLMessage.INFORM);
                informMess.setContent(obs.toString());
            }

            @Override
            public boolean done() {
                return false;
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
            System.out.println("The explorer AID is " + agenteBDI);
        }
        return agenteBDI;
    }
}
