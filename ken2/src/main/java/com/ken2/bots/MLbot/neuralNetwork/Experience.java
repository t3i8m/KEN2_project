package com.ken2.bots.MLbot.neuralNetwork;

public class Experience {
    private final int[] state;
    private final int[] newState;
    private final int action;
    private final int reward;


    public Experience(int[] state, int action, int reward, int[] newState){
        this.state=state;
        this.newState = newState;
        this.action=action;
        this.reward=reward;
    }

    public int[] getState(){
        return this.state;
    }

    public int[] getnewState(){
        return this.newState;
    }

    public int getAction(){
        return this.action;
    }

    public int getReward(){
        return this.reward;
    }
}
