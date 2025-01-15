package com.ken2.bots.DQN_BOT_ML.NeuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Neuron;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.LossFunction;

public class NeuralNetwork {
    private ArrayList<Layer> layers;
    private double learningRate =  0.0005;
    private LossFunction lossFunction;

    public NeuralNetwork(double learningRate, LossFunction lossFunction){
        this.layers = new ArrayList<>();
        this.learningRate =  0.0005;
        this.lossFunction = lossFunction;
    }

    public void addLayer(Layer layer){
        this.layers.add(layer);
    }

    public double[] forward(double[] inputs){
        double[] output = inputs;
        for(Layer layer:layers){
            output = layer.forward(output);
        }
        return output;
    }

    public void backward(double[] predicted, double[] target){
        double[] gradients = lossFunction.computeGradient(predicted, target);

        for(int i = layers.size()-1;i>=0;i--){
            gradients = layers.get(i).backward(gradients);
        }
    }

    public double[] predict(double[] inputs) {
        return forward(inputs);
    }

    public void train(double[][] inputs, double[][] targets, int epochs) {

        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < inputs.length; i++) {
                double[] predicted = forward(inputs[i]);
                
                totalLoss += lossFunction.computeLoss(predicted, targets[i]);
                
                backward(predicted, targets[i]);
            }
            System.out.println("Epoch " + epoch + " Loss: " + totalLoss / inputs.length);
        }
    }

    public void trainMiniBatch(double[][] inputs, double[][] targets) {
        double totalLoss = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] predicted = forward(inputs[i]);
            totalLoss += lossFunction.computeLoss(predicted, targets[i]);
            backward(predicted, targets[i]);
        }
        // System.out.println("Mini-batch Loss: " + totalLoss / inputs.length);
    }

    
    public double getLearningRate() {
        return this.learningRate;
    }
    
    public LossFunction getLossFunction() {
        return this.lossFunction;
    }
    
    public List<Layer> getLayers() {
        return this.layers;  
    }

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
        // System.out.println("Weights saved to " + "ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\networkWeights.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void loadWeights() {
    try (BufferedReader reader = new BufferedReader(new FileReader("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\networkWeights.txt"))) {
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
        // System.out.println("Weights loaded from " + "ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\NeuralNetwork\\weights\\networkWeights.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    
    
}
