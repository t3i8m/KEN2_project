package com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents;


import java.util.ArrayList;
import java.util.List;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;

/**
 * Represents a layer in a neural network.
 * Each layer contains multiple neurons and is associated with an activation function.
 */
public class Layer {
    private ArrayList<Neuron> neurons;
    private ActivationFunction activationFunction;
    private double[] lastInputs;

    /**
     * Constructor to initialize a layer with a specific number of neurons and input size.
     *
     * @param numberOfNeurons      The number of neurons in the layer.
     * @param inputSize            The size of the input vector for each neuron.
     * @param activationFunction   The activation function to be applied to each neuron.
     */
    public Layer(int numberOfNeurons, int inputSize, ActivationFunction activationFunction){
        this.activationFunction = activationFunction;
        this.neurons = new ArrayList<>();
        for(int i = 0; i<numberOfNeurons;i++){
            double[] weights = initializeWeights(inputSize);
            double bias = initializeBias();
            Neuron n = new Neuron(weights, bias, activationFunction);
            this.neurons.add(n);

        }
    }

    /**
     * Performs a forward pass through the layer.
     *
     * @param inputs The input vector to the layer.
     * @return The output vector of the layer.
     */
    public double[] forward(double[] inputs){
        lastInputs = inputs.clone();

        double[] outputs = new double[neurons.size()];
        for (double input : inputs) {
            if (Double.isNaN(input) || Double.isInfinite(input)) {
                throw new IllegalArgumentException("Invalid input detected: " + input);
            }
        }
        for(int i = 0; i<neurons.size();i++){
            try{
                double outputValue = neurons.get(i).activate(inputs);
                outputs[i] = outputValue;
            } catch(Exception ex){
                System.out.println(ex);
                System.exit(0);
            }
        }
        return outputs;

    }

   
    /**
     * Performs a backward pass through the layer for backpropagation.
     *
     * @param gradients The gradients of the loss with respect to the layer's output.
     * @return The gradients of the loss with respect to the layer's input.
     */
    public double[] backward(double[] gradients) {
        if (gradients.length != neurons.size()) {
            throw new IllegalArgumentException("Mismatch: gradients length = " 
                + gradients.length + ", neurons size = " + neurons.size());
        }
    
        double[] newGradients = new double[lastInputs.length]; 
        
        for (int i = 0; i < neurons.size(); i++) {
            Neuron neuron = neurons.get(i);
            double neuronDelta = gradients[i] * activationFunction.derivative(neuron.getInput());
    
            for (int j = 0; j < neuron.getWeights().length; j++) {
                newGradients[j] += neuronDelta * lastInputs[j];
            }
    
            double[] neuronGradients = new double[neuron.getWeights().length];
            for (int j = 0; j < neuron.getWeights().length; j++) {
                neuronGradients[j] = neuronDelta * lastInputs[j];
            }
    
            neuron.updateWeights(neuronGradients,  0.0005);  
            // double newBias = neuron.getBias() -  0.0005 * neuronDelta;
            double clippedDelta = Math.max(-1.0, Math.min(1.0, neuronDelta)); 
            double newBias = neuron.getBias() - 0.0005 * clippedDelta;
            neuron.setBias(newBias);
                        // System.out.println(neuron.getBias());
        }
    
        return newGradients;
    }
    
    

    /**
     * Initializes random biases for the neurons in the layer.
     *
     * @return A small random bias value.
     */
    private double initializeBias() {
        return Math.random() * 0.02 - 0.01; //  -0.01  0.01
    }
    
     /**
     * Initializes random weights for the neurons in the layer.
     *
     * @param inputSize The size of the input vector for each neuron.
     * @return An array of random weights.
     */
    private double[] initializeWeights(int inputSize) {
        double[] weights = new double[inputSize];
        for (int i = 0; i < inputSize; i++) {
            weights[i] = Math.random() * 0.02 - 0.01; 
        }
        return weights;
    }
    
    /**
     * Returns the list of neurons in the layer.
     *
     * @return A list of neurons.
     */
    public List<Neuron> getNeurons(){
        return this.neurons;
    }

    /**
     * Returns the activation function of the layer.
     *
     * @return The activation function.
     */
    public ActivationFunction getActivationFunction() {
        return this.activationFunction;
    }
}
