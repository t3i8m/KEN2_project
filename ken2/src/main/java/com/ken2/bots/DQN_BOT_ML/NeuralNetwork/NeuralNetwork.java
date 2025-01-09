package com.ken2.bots.DQN_BOT_ML.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.Layer;

public class NeuralNetwork {
    private ArrayList<Layer> layers;
    private double learningRate;

    public NeuralNetwork(double learningRate){
        this.layers = new ArrayList<>();
        this.learningRate = learningRate;
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
}
