package com.ken2.bots.DQN_BOT_ML.parallel_trainning;

import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.DQN_BOT_ML.utils.ReplayBuffer;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.headless.Headless;

class GameWorker implements Runnable {
    private final int episodes;
    private final ReplayBuffer replayBuffer;
    private final DQN_BOT dqnBot; 

    public GameWorker(int episodes, ReplayBuffer replayBuffer, DQN_BOT dqnBot) {
        this.episodes = episodes;
        this.replayBuffer = replayBuffer;
        this.dqnBot = dqnBot;
    }

    @Override
    public void run() {
        for (int i = 0; i < episodes; i++) {
            Headless gameSimulation = new Headless(1, dqnBot, new RuleBasedBot("black"));

           
            gameSimulation.runGamesWithoutStats(); 

        }
    }
}
