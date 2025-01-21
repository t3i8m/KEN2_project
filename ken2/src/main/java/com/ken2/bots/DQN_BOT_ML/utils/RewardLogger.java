package com.ken2.bots.DQN_BOT_ML.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RewardLogger {
    private int episode;
    private double cumulativeReward;
    private PrintWriter writer;

    public RewardLogger(String filename) {
        try {
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("Episode,Cumulative Reward");
        } catch (IOException e) {
            System.err.println("Error creating RewardLogger: " + e.getMessage());
        }
    }

    public void resetEpisode() {
        episode++;
        cumulativeReward = 0;
    }

    public void addReward(double reward) {
        cumulativeReward = reward;
    }

    public void logEpisode() {
        writer.println(episode + "," + cumulativeReward);
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}