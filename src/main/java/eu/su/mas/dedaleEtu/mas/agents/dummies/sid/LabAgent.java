package eu.su.mas.dedaleEtu.mas.agents.dummies.sid;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SayHelloBehaviour;
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

public class LabAgent extends AbstractDedaleAgent {
    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 1) set the agent attributes
     * 2) add the behaviours
     */
    protected void setup() {
        super.setup();
        //use them as parameters for your behaviours is you want
        List<Behaviour> lb = new ArrayList<>();


        // MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
        addBehaviour(new startMyBehaviours(this, behavioursList()));
    }

    private List<Behaviour> behavioursList() {
        //use them as parameters for your behaviours is you want
        List<Behaviour> lb = new ArrayList<>();

        lb.add(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("One shot behave");
                try {
                    register();
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }

                //Algoritmo de búsqueda de recursos
                String ubiActual = String.valueOf(getCurrentPosition());
                System.out.println("Mi ubicacion es " + getCurrentPosition().toString());
                System.out.println("Lab agent registered");
            }
        });

        return lb;
    }

    private void register() throws FIPAException {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Lab");
        sd.setType("agentExplo");
        dfd.addServices(sd);
        DFService.register(this,dfd);
    }

    private AID searchAgent() throws FIPAException {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType("agentCollect");
        template.addServices(templateSd);

        SearchConstraints sc = new SearchConstraints();
        // We want to receive 10 results at most
        sc.setMaxResults(new Long(10));

        DFAgentDescription[] results = DFService.search(this, template, sc);

        AID provider = null;
        if (results.length > 0) {
            DFAgentDescription dfd = results[0];
            provider = dfd.getName();
            System.out.println("The explorer AID is " + provider);
        }
        return provider;
    }

    /**
     * This method is automatically called after doDelete()
     */
    protected void takeDown() {
        super.takeDown();
    }

    /**
     * This method is automatically called before migration.
     * You can add here all the saving you need
     */
    protected void beforeMove() {
        super.beforeMove();
    }

    /**
     * This method is automatically called after migration to reload.
     * You can add here all the info regarding the state you want your agent to restart from
     */
    protected void afterMove() {
        super.afterMove();
    }
}
