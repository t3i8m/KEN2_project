package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents;

import java.lang.reflect.Array;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.List;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;

public class Layer {
    private ArrayList<Neuron> neurons;
    private ActivationFunction activationFunction;
    private List<double[]> layerInputs;


    public Layer(int numberOfNeurons, int inputSize, ActivationFunction activationFunction){
        this.activationFunction = activationFunction;
        this.neurons = new ArrayList<>();
        for(int i = 0; i<numberOfNeurons;i++){
            double[] weights = initializeWeights(inputSize);
            double bias = initializeBias();
            Neuron n = new Neuron(weights, bias, activationFunction);
            this.neurons.add(n);
            this.layerInputs = new ArrayList<>();

        }
    }

    public double[] forward(double[] inputs){
        layerInputs.add(inputs.clone());

        double[] outputs = new double[neurons.size()];
        
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

    public double[] backward(double[] gradients, double learningRate){
        // TODO: implement it

        throw new UnsupportedAddressTypeException();
    }


    private double initializeBias(){
        return Math.random()-0.5;
    }

    private double[] initializeWeights(int inputSize){
        double[] weights = new double[inputSize];
        for(int i = 0; i<inputSize;i++){
            weights[i] =  Math.random() - 0.5; //from -0.5 to 0.5
        }
        return weights;
    }

    public List<Neuron> getNeurons(){
        return this.neurons;
    }
}
