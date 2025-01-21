package com.ken2.bots.DQN_BOT_ML.utils;

import com.ken2.bots.DQN_BOT_ML.botComponents.DQNTrainer;
import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.DQN_BOT_ML.utils.Hyperparameters;

public class ExperimentalConfig {
    public static void main(String[] args) {
        Hyperparameters[] configs = {
                // Config 1-20: Low learning rate variations
                new Hyperparameters("white", 0.0001, 0.90, 1.0, 500, 16),
                new Hyperparameters("white", 0.0001, 0.90, 0.8, 1000, 16),
                new Hyperparameters("white", 0.0001, 0.95, 0.6, 1500, 32),
                new Hyperparameters("white", 0.0001, 0.95, 0.4, 2000, 32),
                new Hyperparameters("white", 0.0001, 0.99, 0.2, 3000, 64),
                new Hyperparameters("white", 0.0001, 0.99, 0.1, 5000, 64),
                new Hyperparameters("white", 0.0005, 0.90, 1.0, 1000, 16),
                new Hyperparameters("white", 0.0005, 0.90, 0.8, 2000, 32),
                new Hyperparameters("white", 0.0005, 0.95, 0.6, 3000, 32),
                new Hyperparameters("white", 0.0005, 0.95, 0.4, 5000, 64),
                new Hyperparameters("white", 0.0005, 0.99, 0.2, 10000, 64),
                new Hyperparameters("white", 0.0005, 0.99, 0.1, 15000, 128),
                new Hyperparameters("white", 0.001, 0.90, 1.0, 2000, 32),
                new Hyperparameters("white", 0.001, 0.90, 0.8, 5000, 64),
                new Hyperparameters("white", 0.001, 0.95, 0.6, 10000, 64),
                new Hyperparameters("white", 0.001, 0.95, 0.4, 15000, 128),
                new Hyperparameters("white", 0.001, 0.99, 0.2, 20000, 128),
                new Hyperparameters("white", 0.001, 0.99, 0.1, 30000, 256),
                new Hyperparameters("white", 0.005, 0.90, 0.8, 3000, 32),
                new Hyperparameters("white", 0.005, 0.95, 0.6, 5000, 64),

                // Config 21-40: Medium learning rate variations
                new Hyperparameters("white", 0.005, 0.95, 0.4, 10000, 64),
                new Hyperparameters("white", 0.005, 0.99, 0.2, 15000, 128),
                new Hyperparameters("white", 0.005, 0.99, 0.1, 20000, 256),
                new Hyperparameters("white", 0.01, 0.90, 1.0, 500, 16),
                new Hyperparameters("white", 0.01, 0.90, 0.8, 1000, 32),
                new Hyperparameters("white", 0.01, 0.95, 0.6, 1500, 32),
                new Hyperparameters("white", 0.01, 0.95, 0.4, 2000, 64),
                new Hyperparameters("white", 0.01, 0.99, 0.2, 3000, 64),
                new Hyperparameters("white", 0.01, 0.99, 0.1, 5000, 128),
                new Hyperparameters("white", 0.02, 0.90, 1.0, 1000, 16),
                new Hyperparameters("white", 0.02, 0.90, 0.8, 2000, 32),
                new Hyperparameters("white", 0.02, 0.95, 0.6, 3000, 32),
                new Hyperparameters("white", 0.02, 0.95, 0.4, 5000, 64),
                new Hyperparameters("white", 0.02, 0.99, 0.2, 10000, 64),
                new Hyperparameters("white", 0.02, 0.99, 0.1, 15000, 128),
                new Hyperparameters("white", 0.03, 0.90, 1.0, 2000, 32),
                new Hyperparameters("white", 0.03, 0.90, 0.8, 5000, 64),
                new Hyperparameters("white", 0.03, 0.95, 0.6, 10000, 64),
                new Hyperparameters("white", 0.03, 0.95, 0.4, 15000, 128),
                new Hyperparameters("white", 0.03, 0.99, 0.2, 20000, 256),

                // Config 41-60: High learning rate variations
                new Hyperparameters("white", 0.03, 0.99, 0.1, 30000, 256),
                new Hyperparameters("white", 0.05, 0.90, 0.8, 3000, 32),
                new Hyperparameters("white", 0.05, 0.95, 0.6, 5000, 64),
                new Hyperparameters("white", 0.05, 0.95, 0.4, 10000, 64),
                new Hyperparameters("white", 0.05, 0.99, 0.2, 15000, 128),
                new Hyperparameters("white", 0.05, 0.99, 0.1, 20000, 256),
                new Hyperparameters("white", 0.08, 0.90, 1.0, 500, 16),
                new Hyperparameters("white", 0.08, 0.90, 0.8, 1000, 32),
                new Hyperparameters("white", 0.08, 0.95, 0.6, 1500, 32),
                new Hyperparameters("white", 0.08, 0.95, 0.4, 2000, 64),
                new Hyperparameters("white", 0.08, 0.99, 0.2, 3000, 64),
                new Hyperparameters("white", 0.08, 0.99, 0.1, 5000, 128),
                new Hyperparameters("white", 0.1, 0.90, 1.0, 1000, 16),
                new Hyperparameters("white", 0.1, 0.90, 0.8, 2000, 32),
                new Hyperparameters("white", 0.1, 0.95, 0.6, 3000, 32),
                new Hyperparameters("white", 0.1, 0.95, 0.4, 5000, 64),
                new Hyperparameters("white", 0.1, 0.99, 0.2, 10000, 64),
                new Hyperparameters("white", 0.1, 0.99, 0.1, 15000, 128),
                new Hyperparameters("white", 0.2, 0.90, 1.0, 2000, 32),
                new Hyperparameters("white", 0.2, 0.90, 0.8, 5000, 64),

                // Config 61-100: Exploration rate variations
                new Hyperparameters("white", 0.2, 0.95, 0.6, 10000, 64),
                new Hyperparameters("white", 0.2, 0.95, 0.4, 15000, 128),
                new Hyperparameters("white", 0.2, 0.99, 0.2, 20000, 256),
                new Hyperparameters("white", 0.2, 0.99, 0.1, 30000, 256),
                new Hyperparameters("white", 0.3, 0.90, 0.8, 3000, 32),
                new Hyperparameters("white", 0.3, 0.95, 0.6, 5000, 64),
                new Hyperparameters("white", 0.3, 0.95, 0.4, 10000, 64),
                new Hyperparameters("white", 0.3, 0.99, 0.2, 15000, 128),
                new Hyperparameters("white", 0.3, 0.99, 0.1, 20000, 256),
                new Hyperparameters("white", 0.5, 0.90, 1.0, 500, 16),
                new Hyperparameters("white", 0.5, 0.90, 0.8, 1000, 32),
                new Hyperparameters("white", 0.5, 0.95, 0.6, 1500, 32),
                new Hyperparameters("white", 0.5, 0.95, 0.4, 2000, 64),
                new Hyperparameters("white", 0.5, 0.99, 0.2, 3000, 64),
                new Hyperparameters("white", 0.5, 0.99, 0.1, 5000, 128),
                new Hyperparameters("white", 1.0, 0.90, 1.0, 2000, 32),
                new Hyperparameters("white", 1.0, 0.90, 0.8, 5000, 64),
                new Hyperparameters("white", 1.0, 0.95, 0.6, 10000, 64),
                new Hyperparameters("white", 1.0, 0.95, 0.4, 15000, 128),
                new Hyperparameters("white", 1.0, 0.99, 0.2, 20000, 256),
                new Hyperparameters("white", 1.0, 0.99, 0.1, 30000, 256)
        };

        int configNumber = 1;

        for (Hyperparameters config : configs) {
            DQN_BOT dqnBot = new DQN_BOT(config);
            DQNTrainer trainer = new DQNTrainer(dqnBot, 1000, 100000,  0.999, "alphabeta", configNumber);
            configNumber++;

            trainer.train();
        }
    }

}
