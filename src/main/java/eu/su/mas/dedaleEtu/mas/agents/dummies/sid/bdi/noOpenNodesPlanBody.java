package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.BeliefGoalPlanBody;

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
            setEndState(Plan.EndState.SUCCESSFUL);
        }
    }
}
