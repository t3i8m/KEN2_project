package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction;

public class MSE implements LossFunction {
    @Override
    public double computeLoss(double[] predicted, double[] target) {
        double loss = 0;
        for (int i = 0; i < predicted.length;i++){
            double error = target[i] - predicted[i];
            loss+=Math.pow(error,2);
        }
        return loss / predicted.length;
    }

    @Override
    public double[] computeGradient(double[] predicted, double[] target) {

       double[] gradient = new double[predicted.length];
       for (int i  =0; i < predicted.length;i++){
           gradient[i]=2*(predicted[i]-target[i])/predicted.length;
       }
       return gradient;
    }
}
