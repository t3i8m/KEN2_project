package com.ken2.bots.DQN_BOT_ML.utils;

public class Hyperparameters {
    public double learningRate;
    public double gamma;
    public double epsilon;
    public int replayBufferSize;
    public int batchSize;

    public Hyperparameters(double learningRate, double gamma, double epsilon, int replayBufferSize, int batchSize) {
        this.learningRate = learningRate;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.replayBufferSize = replayBufferSize;
        this.batchSize = batchSize;
    }
}
