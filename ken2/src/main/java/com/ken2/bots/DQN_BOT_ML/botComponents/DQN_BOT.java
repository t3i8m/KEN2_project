package com.ken2.bots.DQN_BOT_ML.botComponents;

import java.util.ArrayDeque;
import java.util.Random;

import com.ken2.bots.BotAbstract;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.NeuralNetwork;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.MSE;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class DQN_BOT  extends BotAbstract{
    private NeuralNetwork qNetwork;
    private double epsilon; //max epsilon for the egreedy
    private double epsilonMin; // min epsilon for the egreedy
    private double epsilonDecay; // speed of the epsilon decrease
    private double gamma;  //discount for the Q update
    private Random random = new Random();

    public DQN_BOT(String color){
        super(color);
        this.qNetwork = new NeuralNetwork(0.01, new MSE());
        this.epsilon = 1.0;
        this.epsilonMin = 0.1;
        this.epsilonDecay = epsilonDecay;
        this.gamma = gamma;

    }

    public DQN_BOT(String color,NeuralNetwork qNetwork, double epsilon, double epsilonMin, double epsilonDecay, double gamma) {
        super(color);

        this.epsilon=epsilon;
        this.epsilonMin = epsilonMin;
        this.epsilonDecay=epsilonDecay;
        this.gamma = gamma;
    }

    // TODO: implement bot makeMove, uses nn to find the best move in the given state
    public Move makeMove(GameState state){
        return new Move(0, 0, null);
    }

    public void storeExperience(double[] state, int action, double reward, double[] nextState) {
        // TODO: implement 

    }

    public void train(int batchSize){
        // TODO: implement bot makeMove, uses nn to find the best move in the given state
    }
}
