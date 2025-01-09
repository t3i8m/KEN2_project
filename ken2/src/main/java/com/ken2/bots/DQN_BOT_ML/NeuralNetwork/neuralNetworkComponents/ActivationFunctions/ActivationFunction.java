package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions;

public interface ActivationFunction {
    double activate(double weightedSum);
    double derivative(double x);
}
