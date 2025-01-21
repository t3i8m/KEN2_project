package com.ken2.bots.DQN_BOT_ML.utils;

import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class Experience {
    private final double[] state;
    private final double[] newState;
    private final int action;
    private final double reward;
    private double cumulativeReward;
    public Move chosenMove;
    public GameState stateP;
    public GameState nextState;



    public Experience(double[] state, int action, double reward, double[] newState, Move chosenMove, GameState stateP, GameState nextState){
        this.state=state;
        this.newState = newState;
        this.action=action;
        this.reward=reward;
        this.chosenMove=chosenMove;
        this.stateP = stateP;
        this.nextState=nextState;
        this.cumulativeReward = reward; // Initialise cumulative reward with the current reward
    }

    public void updateCumulativeReward(double newReward) {
        this.cumulativeReward += newReward;
    }

    public double getCumulativeReward() {
        return this.cumulativeReward;
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
