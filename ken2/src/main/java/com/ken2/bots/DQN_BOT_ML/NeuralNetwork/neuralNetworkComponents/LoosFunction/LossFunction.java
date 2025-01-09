package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction;

public interface LossFunction {
    double computeLoss(double[] predicted, double[] target);
    double[] computeGradient(double[] predicted, double[] target);
}
