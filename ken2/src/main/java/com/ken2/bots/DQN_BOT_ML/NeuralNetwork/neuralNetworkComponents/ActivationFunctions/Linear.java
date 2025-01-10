package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions;

public class Linear implements ActivationFunction {
    @Override
    public double activate(double weightedSum) {
        return weightedSum;

    }

    @Override
    public double derivative(double x) {
        return 1;
    }
}
