package com.ken2.bots.DQN_BOT_ML.botComponents;

public class DQN_MAIN {
    public static void main(String[] args) {
        DQN_BOT dqnBot = new DQN_BOT("white");
        DQNTrainer trainer = new DQNTrainer(dqnBot, 100, 100000,  0.999, "alphabeta"); 

       


        trainer.train();
    }
}
