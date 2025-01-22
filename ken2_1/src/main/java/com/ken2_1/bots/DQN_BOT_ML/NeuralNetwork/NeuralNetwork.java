package com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Neuron;
import com.ken2_1.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.LossFunction;

/**
 * Represents a feedforward neural network with multiple layers.
 * Supports forward and backward propagation, training, and weight management.
 */
public class NeuralNetwork {
    private ArrayList<Layer> layers;
    private double learningRate =  0.0005;
    private LossFunction lossFunction;

    /**
     * Constructs a new neural network.
     *
     * @param learningRate  The learning rate for training.
     * @param lossFunction  The loss function used to compute errors.
     */
    public NeuralNetwork(double learningRate, LossFunction lossFunction){
        this.layers = new ArrayList<>();
        this.learningRate =  0.0005;
        this.lossFunction = lossFunction;
    }

    /**
     * Adds a new layer to the neural network.
     *
     * @param layer The layer to add.
     */
    public void addLayer(Layer layer){
        this.layers.add(layer);
    }

    /**
     * Performs a forward pass through the network.
     *
     * @param inputs The input vector to the network.
     * @return The output vector from the network.
     */
    public double[] forward(double[] inputs){
        double[] output = inputs;
        for(Layer layer:layers){
            output = layer.forward(output);
        }
        return output;
    }

    /**
     * Performs a backward pass through the network.
     *
     * @param predicted The predicted outputs from the network.
     * @param target    The target (ground truth) outputs.
     */
    public void backward(double[] predicted, double[] target){
        double[] gradients = lossFunction.computeGradient(predicted, target);

        for(int i = layers.size()-1;i>=0;i--){
            gradients = layers.get(i).backward(gradients);
        }
    }

    /**
     * Predicts the output for a given input using the network.
     *
     * @param inputs The input vector.
     * @return The predicted output vector.
     */
    public double[] predict(double[] inputs) {
        return forward(inputs);
    }

    /**
     * Trains the network using a dataset for a specified number of epochs.
     *
     * @param inputs  The input data.
     * @param targets The target (ground truth) data.
     * @param epochs  The number of training iterations.
     */
    public void train(double[][] inputs, double[][] targets, int epochs) {

        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < inputs.length; i++) {
                double[] predicted = forward(inputs[i]);
                
                totalLoss += lossFunction.computeLoss(predicted, targets[i]);
                
                backward(predicted, targets[i]);
            }
        }
    }

    /**
     * Trains the network using a mini-batch of data.
     *
     * @param inputs  The input data for the mini-batch.
     * @param targets The target data for the mini-batch.
     */
    public void trainMiniBatch(double[][] inputs, double[][] targets) {
        double totalLoss = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] predicted = forward(inputs[i]);
            totalLoss += lossFunction.computeLoss(predicted, targets[i]);
            backward(predicted, targets[i]);
        }
    }

    /**
     * Returns the learning rate of the neural network.
     * The learning rate determines the step size during weight updates.
     *
     * @return The learning rate value.
     */
    public double getLearningRate() {
        return this.learningRate;
    }

    /**
     * Returns the loss function used by the neural network.
     * The loss function is used to measure the error between predicted and target values.
     *
     * @return The loss function instance.
     */
    public LossFunction getLossFunction() {
        return this.lossFunction;
    }
    
    /**
     * Returns the list of layers in the neural network.
     * Each layer contains a collection of neurons and defines the structure of the network.
     *
     * @return A list of layers in the neural network.
     */
    public List<Layer> getLayers() {
        return this.layers;  
    }

    /**
     * Saves the network's weights and biases to a file.
     */
    public void saveWeights() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\networkWeights.txt"))) {
            writer.write(String.valueOf(this.layers.size()));
            writer.newLine();


            for (Layer layer : this.layers) {
                int neuronCount = layer.getNeurons().size();
                writer.write(String.valueOf(neuronCount));
                writer.newLine();

                for (Neuron neuron : layer.getNeurons()) {
                    double[] weights = neuron.getWeights();
                    for (int i = 0; i < weights.length; i++) {
                        writer.write(String.valueOf(weights[i]));
                        if (i < weights.length - 1) {
                            writer.write(" ");
                        }
                    }
                    writer.newLine();

                    writer.write(String.valueOf(neuron.getBias()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads the network's weights and biases from a file.
     */
    public void loadWeights() {
        try (BufferedReader reader = new BufferedReader(new FileReader("ken2_1\\src\\main\\java\\com\\ken2_1\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\networkWeights.txt"))) {
            int numLayers = Integer.parseInt(reader.readLine().trim());
            if (numLayers != this.layers.size()) {

                throw new IllegalStateException("Layer count mismatch: file has " 
                        + numLayers + ", but network has " + this.layers.size());
            }

            for (int layerIndex = 0; layerIndex < numLayers; layerIndex++) {
                Layer layer = this.layers.get(layerIndex);

                int neuronCount = Integer.parseInt(reader.readLine().trim());
                if (neuronCount != layer.getNeurons().size()) {
                    throw new IllegalStateException("Neuron count mismatch in layer " + layerIndex);
                }

                for (int neuronIndex = 0; neuronIndex < neuronCount; neuronIndex++) {
                    Neuron neuron = layer.getNeurons().get(neuronIndex);

                    String[] weightStr = reader.readLine().trim().split(" ");
                    double[] weights = neuron.getWeights(); 
                    if (weightStr.length != weights.length) {
                        throw new IllegalStateException("Weights count mismatch in layer " 
                            + layerIndex + " neuron " + neuronIndex);
                    }
                    for (int i = 0; i < weights.length; i++) {
                        weights[i] = Double.parseDouble(weightStr[i]);
                    }

                    double bias = Double.parseDouble(reader.readLine().trim());
                    neuron.setBias(bias);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    
}
