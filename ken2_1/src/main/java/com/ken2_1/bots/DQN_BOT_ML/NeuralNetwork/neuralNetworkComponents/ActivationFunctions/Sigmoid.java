package com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions;

public class Sigmoid implements ActivationFunction{
    @Override
    public double activate(double weightedSum) {
        if (weightedSum>0) {
            return 1 / (1 + Math.exp(-weightedSum));
        }
        else{
            double expX = Math.exp(weightedSum);
            return expX/(1 + expX);
        }

    }

    @Override
    public double derivative(double x) {
        double sigmoid =activate(x);
        return sigmoid * (1-sigmoid);
    }
}
