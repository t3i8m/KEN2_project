/**
 * Commenting out all the code to remove potential conflicts when merging the test branch with the main branch
 */

/*
package com.ken2.bots.DQN_BOT_ML.utils;

import com.ken2.bots.DQN_BOT_ML.botComponents.DQNTrainer;
import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;

public class ExperimentalConfig {
    public static void main(String[] args) {
        Hyperparameters[] configs = {
                // Low learning rate configurations
                new Hyperparameters("white", 0.0001, 0.90, 1.0, 500, 16),
                new Hyperparameters("white", 0.0001, 0.90, 0.8, 1000, 32),
                new Hyperparameters("white", 0.0001, 0.95, 0.6, 1500, 32),
                new Hyperparameters("white", 0.0001, 0.95, 0.4, 2000, 64),
                new Hyperparameters("white", 0.0001, 0.99, 0.2, 3000, 64),
                new Hyperparameters("white", 0.0001, 0.99, 0.1, 5000, 128),

                // Medium learning rate configurations
                new Hyperparameters("white", 0.0005, 0.90, 1.0, 1000, 32),
                new Hyperparameters("white", 0.0005, 0.90, 0.8, 2000, 64),
                new Hyperparameters("white", 0.0005, 0.95, 0.6, 3000, 64),
                new Hyperparameters("white", 0.0005, 0.95, 0.4, 5000, 128),
                new Hyperparameters("white", 0.0005, 0.99, 0.2, 10000, 256),
                new Hyperparameters("white", 0.0005, 0.99, 0.1, 15000, 256),

                // High learning rate configurations
                new Hyperparameters("white", 0.001, 0.90, 1.0, 2000, 64),
                new Hyperparameters("white", 0.001, 0.90, 0.8, 5000, 128),
                new Hyperparameters("white", 0.001, 0.95, 0.6, 10000, 128),
                new Hyperparameters("white", 0.001, 0.95, 0.4, 15000, 256),
                new Hyperparameters("white", 0.001, 0.99, 0.2, 20000, 256),
                new Hyperparameters("white", 0.001, 0.99, 0.1, 30000, 512),

                // Very high learning rate configurations
                new Hyperparameters("white", 0.005, 0.90, 0.8, 3000, 64),
                new Hyperparameters("white", 0.005, 0.95, 0.6, 5000, 128),
                new Hyperparameters("white", 0.005, 0.95, 0.4, 10000, 256),
                new Hyperparameters("white", 0.005, 0.99, 0.2, 15000, 512),
                new Hyperparameters("white", 0.005, 0.99, 0.1, 20000, 1024),

                // Exploration-focused configurations
                new Hyperparameters("white", 0.01, 0.90, 1.0, 500, 32),
                new Hyperparameters("white", 0.01, 0.90, 0.8, 1000, 64),
                new Hyperparameters("white", 0.01, 0.95, 0.6, 1500, 128),
                new Hyperparameters("white", 0.01, 0.95, 0.4, 2000, 256),
                new Hyperparameters("white", 0.01, 0.99, 0.2, 3000, 512),
                new Hyperparameters("white", 0.01, 0.99, 0.1, 5000, 1024),

                // Higher exploration rate and different replay buffer sizes
                new Hyperparameters("white", 0.02, 0.90, 1.0, 1000, 64),
                new Hyperparameters("white", 0.02, 0.90, 0.8, 2000, 128),
                new Hyperparameters("white", 0.02, 0.95, 0.6, 3000, 256),
                new Hyperparameters("white", 0.02, 0.95, 0.4, 5000, 512),
                new Hyperparameters("white", 0.02, 0.99, 0.2, 10000, 1024),
                new Hyperparameters("white", 0.02, 0.99, 0.1, 15000, 2048)
        };


        int configNumber = 1;

        for (Hyperparameters config : configs) {
            DQN_BOT dqnBot = new DQN_BOT(
                    config.getColor(),
                    config.getLearningRate(),
                    config.getGamma(),
                    config.getEpsilon(),
                    config.getReplayBufferSize(),
                    config.getActionSize()
            );

            DQNTrainer trainer = new DQNTrainer(dqnBot, 1000, 100000, 0.999, "alphabeta", configNumber++);

            System.out.println("Training config #" + configNumber);
            trainer.train();
        }
    }
}*/
