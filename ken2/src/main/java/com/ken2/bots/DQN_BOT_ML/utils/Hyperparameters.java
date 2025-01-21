package com.ken2.bots.DQN_BOT_ML.utils;

public class Hyperparameters {
    private String color;
    private double learningRate;
    private double gamma;
    private double epsilon;
    private int replayBufferSize;
    private int actionSize;

    public Hyperparameters(String color, double learningRate, double gamma, double epsilon, int replayBufferSize, int actionSize) {
        this.color = color;
        this.learningRate = learningRate;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.replayBufferSize = replayBufferSize;
        this.actionSize = actionSize;
    }

    public String getColor() { return color; }
    public double getLearningRate() { return learningRate; }
    public double getGamma() { return gamma; }
    public double getEpsilon() { return epsilon; }
    public int getReplayBufferSize() { return replayBufferSize; }
    public int getActionSize() { return actionSize; }
}
