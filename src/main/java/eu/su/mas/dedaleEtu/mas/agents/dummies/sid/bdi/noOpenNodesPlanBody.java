package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.List;

public class noOpenNodesPlanBody extends BeliefGoalPlanBody  {
    @Override
    protected void execute() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BDIAgent2 agente = (BDIAgent2) this.myAgent;
        System.out.println("Open nodes in FIPA " + agente.getOpenNodes());
        if (agente.getClosedNodes() != null) {

            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(new AID("Lab", AID.ISLOCALNAME));
            request.setContent("16");
            //List<Behaviour> lb= new ArrayList<>();
           /* lb.add( new AchieveREInitiator(agente, request) {
                protected void handleInform(ACLMessage inform) {
                    System.out.println("Protocol finished. Rational Effect achieved.Received the following message:" +inform); }
            });
            agente.addBehaviour(new startMyBehaviours((AbstractDedaleAgent) this.getAgent(),lb));*/
            //agente.send(request);

            if (agente.getOpenNodes().isEmpty()) {
                setEndState(Plan.EndState.SUCCESSFUL);
            }
        }
    }
}
