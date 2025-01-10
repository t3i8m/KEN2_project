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
    private Random random;
    private int actionSize;


    public DQN_BOT(String color){
        super(color);
        this.qNetwork = new NeuralNetwork(0.01, new MSE());
        this.epsilon = 1;
        this.epsilonMin = 0.01;
        this.epsilonDecay = 0.995;
        this.gamma = 0.99;
        this.random = new Random();
        this.actionSize = 10; //CAN BE ADJUSTED;

    }

    public DQN_BOT(String color,NeuralNetwork qNetwork, double epsilon, double epsilonMin, double epsilonDecay, double gamma) {
        super(color);
        this.qNetwork = qNetwork;
        this.epsilon=epsilon;
        this.epsilonMin = epsilonMin;
        this.epsilonDecay=epsilonDecay;
        this.gamma = gamma;
        this.random = new Random();
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

    public int chooseAction(double[] qValues){
        if(random.nextDouble() < epsilon){//если меньше, выбирается случайное действие (exploration)
            return random.nextInt(actionSize);
        }
        else{
            return argMax(qValues);//выбирается макс Q значение(эксплотация)
        }
    }
    //finds an index with the highest q-value
    public int argMax(double[] qValues){
        int bestAction = 0;
        for (int i =1; i <qValues.length;i++){
            if(qValues[i]>qValues[bestAction]){
                bestAction=i;
            }
        }
        return bestAction;
    }


}
