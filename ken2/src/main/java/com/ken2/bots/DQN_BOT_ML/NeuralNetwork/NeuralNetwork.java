package com.ken2.bots.DQN_BOT_ML.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction.LossFunction;

public class NeuralNetwork {
    private ArrayList<Layer> layers;
    private double learningRate;
    private LossFunction lossFunction;

    public NeuralNetwork(double learningRate, LossFunction lossFunction){
        this.layers = new ArrayList<>();
        this.learningRate = learningRate;
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
    
}
