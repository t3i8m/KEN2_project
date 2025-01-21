package com.ken2.bots.DQN_BOT_ML.botComponents;

public class DQN_MAIN {
    public static void main(String[] args) {
        DQN_BOT dqnBot = new DQN_BOT("white", 0.2, 0.95, 0.6, 10000, 64);
        DQNTrainer trainer = new DQNTrainer(dqnBot, 1000, 100000,  0.999, "alphabeta", 1);

       


        trainer.train();
    }
}
