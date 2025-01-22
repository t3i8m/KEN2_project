package com.ken2_1.bots.DQN_BOT_ML.botComponents;

/**
 * Entry point for training and testing the DQN bot.
 * This class initializes the bot and starts the training process.
 */
public class DQN_MAIN {

     /**
     * The main method where the DQN bot and its training process are initialized.
     *
     */
    public static void main(String[] args) {
        DQN_BOT dqnBot = new DQN_BOT("white");

        // Initialize the DQNTrainer with the following parameters:
        // - dqnBot: The bot to be trained
        // - episodes: Number of training episodes
        // - maxStepsPerEpisode: Maximum steps allowed per episode
        // - discountFactor: Discount factor (gamma) for future rewards
        // - opponentType: Type of opponent (e.g., "alphabeta")
        DQNTrainer trainer = new DQNTrainer(dqnBot, 100, 100000,  0.999, "alphabeta"); 

        trainer.train();
    }
}
