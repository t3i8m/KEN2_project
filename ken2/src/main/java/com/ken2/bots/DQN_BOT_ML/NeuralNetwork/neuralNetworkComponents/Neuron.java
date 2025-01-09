package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents;

import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ActivationFunction;
import com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.ActivationFunctions.ReLu;

public class Neuron {
    private double[] weights;
    private double bias;
    private ActivationFunction activationFunction;
    private double inputSum; // before activation
    private double outputSum; // after activation
    private double delta; // error of the neuron
    private double[] lastInputs;


    public Neuron(double[] weights, double bias, ActivationFunction activationFunction){
        this.weights = weights;
        this.activationFunction = activationFunction;
    }

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

    public void updateWeights(double learningRate){
        for(int i = 0; i<weights.length;i++){
            weights[i] -= learningRate*delta*this.lastInputs[i];
        }
        bias-=learningRate* delta;
    }
    public double[] computeInputGradient(){
        double[] inputGradient = new double[weights.length];
        for(int i =0; i < weights.length;i++){
            inputGradient[i]=delta*weights[i];
        }
        return inputGradient;
    }


    public double[] getWeights(){
        return this.weights;
    }

    public double getOutput(){
        return this.outputSum;
    }

    public double getInput(){
        return this.inputSum;
    }

    public void setBias(double bias){
        this.bias = bias;
    }

    public void setDelta(double delta){
        this.delta = delta;
    }

    public double getDelta(){
        return this.delta;
    }

}
