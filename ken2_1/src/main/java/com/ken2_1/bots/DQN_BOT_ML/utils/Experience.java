package com.ken2_1.bots.DQN_BOT_ML.utils;

import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

/**
 * Represents a single experience (state-action-reward-nextState tuple) used in reinforcement learning.
 * This class encapsulates the data required to train a neural network for decision-making.
 */
public class Experience {
    private final double[] state;
    private final double[] newState;
    private final int action;
    private final double reward;
    public Move chosenMove;
    public GameState stateP;
    public GameState nextState;


    /**
     * Constructs an Experience object with the specified parameters.
     *
     * @param state      The state of the environment before the action.
     * @param action     The action taken by the agent.
     * @param reward     The reward received for the action.
     * @param newState   The state of the environment after the action.
     * @param chosenMove The specific move corresponding to the action.
     * @param stateP     The game state before the action.
     * @param nextState  The game state after the action.
     */
    public Experience(double[] state, int action, double reward, double[] newState, Move chosenMove, GameState stateP, GameState nextState){
        this.state=state;
        this.newState = newState;
        this.action=action;
        this.reward=reward;
        this.chosenMove=chosenMove;
        this.stateP = stateP;
        this.nextState=nextState;
    }

    /**
     * Returns the state of the environment before the action.
     *
     * @return The state as a vector.
     */
    public double[] getState(){
        return this.state;
    }
    /**
     * Returns the state of the environment after the action.
     *
     * @return The new state as a vector.
     */
    public double[] getnewState(){
        return this.newState;
    }

    /**
     * Returns the action taken by the agent.
     *
     * @return The action as an integer.
     */
    public int getAction(){
        return this.action;
    }

    /**
     * Returns the reward received for the action.
     *
     * @return The reward value.
     */
    public double getReward(){
        return this.reward;
    }
    
    /**
     * Returns the next state of the environment.
     *
     * @return The next state as a vector.
     */
    public double[] getNextState(){
        return this.newState;
    }
}
