package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions;

public class ReLu implements ActivationFunction{
    
    public ReLu(){}

    public double activate(double weightedSum){
        if(weightedSum<=0){
            return 0;
        } else{
            return weightedSum;
        }
    }

    @Override
    public double derivative(double x) {
        if(x<=0){
            return 0;
        } else{
            return x;
        }
    }
}
