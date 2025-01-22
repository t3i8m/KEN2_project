package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction;

/**
 * Interface representing a loss function in a neural network.
 * Loss functions measure the difference between the predicted output and the target output.
 * They are essential for guiding the training process by providing a metric for optimization.
 */
public interface LossFunction {

    /**
     * Computes the loss between the predicted output and the target output.
     *
     * @param predicted The predicted values from the neural network.
     * @param target    The target (ground truth) values.
     * @return The computed loss as a single scalar value.
     */
    double computeLoss(double[] predicted, double[] target);

    /**
     * Computes the gradient of the loss function with respect to the predicted values.
     * This gradient is used during backpropagation to adjust the network's weights.
     *
     * @param predicted The predicted values from the neural network.
     * @param target    The target (ground truth) values.
     * @return An array representing the gradient for each predicted value.
     */
    double[] computeGradient(double[] predicted, double[] target);
}
