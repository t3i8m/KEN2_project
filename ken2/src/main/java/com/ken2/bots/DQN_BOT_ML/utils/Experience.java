package com.ken2.bots.DQN_BOT_ML.utils;

public class Experience {
    private final double[] state;
    private final double[] newState;
    private final int action;
    private final double reward;


    public Experience(double[] state, int action, double reward, double[] newState){
        this.state=state;
        this.newState = newState;
        this.action=action;
        this.reward=reward;
    }

    public double[] getState(){
        return this.state;
    }

    public double[] getnewState(){
        return this.newState;
    }

    public int getAction(){
        return this.action;
    }

    public double getReward(){
        return this.reward;
    }

    public double[] getNextState(){
        return this.newState;
    }
}
