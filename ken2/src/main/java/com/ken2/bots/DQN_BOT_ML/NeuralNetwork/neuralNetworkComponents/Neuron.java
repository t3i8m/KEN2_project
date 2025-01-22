package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ReLu;

/**
 * Represents a single neuron in a neural network.
 * A neuron applies a weighted sum to its inputs, adds a bias, and applies an activation function.
 */
public class Neuron {
    private double[] weights;
    private double bias;
    private ActivationFunction activationFunction;
    private double inputSum; // before activation
    private double outputSum; // after activation
    private double delta; // error of the neuron
    private double[] lastInputs;

    /**
     * Constructs a neuron with specified weights, bias, and activation function.
     *
     * @param weights            The weights for the inputs.
     * @param bias               The bias term for the neuron.
     * @param activationFunction The activation function for the neuron.
     */
    public Neuron(double[] weights, double bias, ActivationFunction activationFunction){
        this.weights = weights;
        this.activationFunction = activationFunction;
    }

    /**
     * Activates the neuron by computing the weighted sum of inputs, adding the bias, 
     * and applying the activation function.
     *
     * @param inputs The input vector for the neuron.
     * @return The output of the neuron after applying the activation function.
     * @throws Exception If the input size does not match the weight size.
     */
    public double activate(double[] inputs) throws Exception{
        if(inputs.length!=weights.length){
            throw new Exception("Input and weights length do not match");
        } 

        inputSum = this.bias;
        for (int i = 0;i <inputs.length;i++){
            double sum = this.weights[i]*inputs[i];
            inputSum+=sum;
        }
        outputSum = activationFunction.activate(inputSum);
        this.lastInputs = inputs.clone();
        return outputSum;


    }

    /**
     * Updates the weights of the neuron using the provided gradients and learning rate.
     *
     * @param gradients    The gradients of the loss with respect to the weights.
     * @param learningRate The learning rate for updating the weights.
     */
    public void updateWeights(double[] gradients, double learningRate) {
        double gradientClipValue = 1.0; 

        for (int i = 0; i < weights.length; i++) {
            gradients[i] = Math.max(-gradientClipValue, Math.min(gradientClipValue, gradients[i])); // Обрезка
            weights[i] -= learningRate * gradients[i];
        }
    }

    /**
     * Returns the bias term of the neuron.
     *
     * @return The bias value.
     */
    public double getBias() {
        return this.bias;
    }
    

    /**
     * Returns the weights of the neuron.
     *
     * @return An array of weights.
     */
    public double[] getWeights(){
        return this.weights;
    }

    /**
     * Returns the inputs used in the last forward pass.
     *
     * @return An array of inputs.
     */
    public double[] getLastInputs(){
        return this.lastInputs;
    }

    /**
     * Returns the output of the neuron from the last forward pass.
     *
     * @return The output value.
     */
    public double getOutput(){
        return this.outputSum;
    }

    /**
     * Returns the input sum of the neuron from the last forward pass.
     *
     * @return The input sum value.
     */
    public double getInput(){
        return this.inputSum;
    }

    /**
     * Sets the bias term of the neuron.
     *
     * @param bias The new bias value.
     */
    public void setBias(double bias){
        this.bias = bias;
    }

    /**
     * Sets the delta (error term) for backpropagation.
     *
     * @param delta The error term.
     */
    public void setDelta(double delta){
        this.delta = delta;
    }
    
    /**
     * Returns the delta (error term) for backpropagation.
     *
     * @return The error term.
     */
    public double getDelta(){
        return this.delta;
    }

}
