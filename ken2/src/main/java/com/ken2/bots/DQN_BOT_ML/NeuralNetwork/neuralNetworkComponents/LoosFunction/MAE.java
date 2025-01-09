package com.ken2.bots.DQN_BOT_ML.NeuralNetwork.neuralNetworkComponents.LoosFunction;

public class MAE implements LossFunction{
    @Override
    public double computeLoss(double[] predicted, double[] target) {
        double loss=0;
        for(int i = 0; i < predicted.length;i++){
            loss+=Math.abs(target[i] - predicted[i]);
        }
        return loss / predicted.length;
    }

    @Override
    public double[] computeGradient(double[] predicted, double[] target) {
      double[] gradient = new double[predicted.length];
      for (int i =0; i < predicted.length;i++ ){
          if (predicted [i]>target[i]){
              gradient[i] = 1 / predicted.length;
          }
          else
              gradient[i] = -1 / predicted.length;
      }
      return gradient;
    }
}
