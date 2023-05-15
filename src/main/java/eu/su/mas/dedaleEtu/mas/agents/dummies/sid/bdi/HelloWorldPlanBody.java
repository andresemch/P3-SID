package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;


import bdi4jade.plan.Plan;
import bdi4jade.plan.planbody.AbstractPlanBody;

public class HelloWorldPlanBody extends AbstractPlanBody {
    private int count=0;
    @Override
    public void action() {
        count++;
        HelloWorldGoal goal = (HelloWorldGoal) getGoal();
        System.out.println("Hello World " + count);
        if(count>9) setEndState(Plan.EndState.FAILED);
    }
}