package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.List;

public class SituatedAgent extends AbstractDedaleAgent {
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
        addBehaviour(new startMyBehaviours(this, lb));
    }

    private AID searchAgent() throws FIPAException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        //templateSd.setType("agentCollect");  !!!!!!!!!!!
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
