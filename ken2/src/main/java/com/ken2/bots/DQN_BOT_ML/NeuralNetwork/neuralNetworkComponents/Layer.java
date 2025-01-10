package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents;

import java.lang.reflect.Array;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.List;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;

public class Layer {
    private ArrayList<Neuron> neurons;
    private ActivationFunction activationFunction;
    private double[] lastInputs;


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

    public double[] forward(double[] inputs){
        lastInputs = inputs.clone();

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

    public double[] backward(double[] gradients){
        double[] currentGradients = new double[neurons.size()];

        for(int i = 0; i<neurons.size();i++){
            Neuron neuron = neurons.get(i);
            double neuronDelta = gradients[i]* activationFunction.derivative(neuron.getInput());
            double[] neuronGradients = new double[neuron.getWeights().length];

            for(int j =0; j<neuron.getWeights().length;j++){
                currentGradients[j] +=neuronDelta*lastInputs[j];
                neuronGradients[j] = neuronDelta * neuron.getLastInputs()[j];
            }

            neuron.updateWeights(neuronGradients, 0.01);

        }
        
        return currentGradients;
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
