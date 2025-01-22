package com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions;

/**
 * Interface representing an activation function in a neural network.
 * Activation functions are applied to the weighted sum of inputs for each neuron
 * to introduce non-linearity into the model.
 */
public interface ActivationFunction {
    /**
     * Applies the activation function to the given weighted sum.
     *
     * @param weightedSum The weighted sum of inputs to a neuron.
     * @return The result of applying the activation function.
     */
    double activate(double weightedSum);

    /**
     * Computes the derivative of the activation function for backpropagation.
     *
     * @param x The input value for which to compute the derivative.
     * @return The derivative of the activation function.
     */
    double derivative(double x);
}
