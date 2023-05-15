package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.core.SingleCapabilityAgent;
import bdi4jade.plan.DefaultPlan;
import bdi4jade.plan.Plan;

public class BDIAgent2 extends SingleCapabilityAgent {
    public BDIAgent2(){
        Plan plan = new DefaultPlan(HelloWorldGoal.class,

                HelloWorldPlanBody.class);
        Plan plan2 = new DefaultPlan(HelloWorldGoal.class,ByeWorldPlanBody.class);
        addGoal(new HelloWorldGoal("!"));
        // 2 goals con el mismo plan se ejecutan de manera concurrente://
        // addGoal(new HelloWorldGoal("2"));
        getCapability().getPlanLibrary().addPlan(plan);
        getCapability().getPlanLibrary().addPlan(plan2);
    }
}
