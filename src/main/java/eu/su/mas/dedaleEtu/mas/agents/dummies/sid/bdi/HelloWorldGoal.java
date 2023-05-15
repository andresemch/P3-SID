package eu.su.mas.dedaleEtu.mas.agents.dummies.sid.bdi;

import bdi4jade.goal.Goal;

public class HelloWorldGoal implements Goal {
    private final String text;
    public HelloWorldGoal (String text){
        this.text= text;
    }
    public String getText(){
        return text;
    }
}
