package com.ken2.bots.DQN_BOT_ML.parallel_trainning;

import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.DQN_BOT_ML.utils.ReplayBuffer;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.headless.Headless;

/**
 * Represents a worker thread responsible for running a set number of game simulations.
 * Each worker operates independently, allowing for parallel training of the DQN bot.
 */
class GameWorker implements Runnable {
    private final int episodes;
    private final ReplayBuffer replayBuffer;
    private final DQN_BOT dqnBot; 

    /**
     * Constructs a GameWorker with the specified parameters.
     *
     * @param episodes      The number of episodes to simulate.
     * @param replayBuffer  The shared replay buffer for experience storage.
     * @param dqnBot        The DQN bot being trained.
     */
    public GameWorker(int episodes, ReplayBuffer replayBuffer, DQN_BOT dqnBot) {
        this.episodes = episodes;
        this.replayBuffer = replayBuffer;
        this.dqnBot = dqnBot;
    }

    /**
     * Executes the simulation of games in a separate thread.
     * For each episode:
     * 1. Initializes a game simulation with the DQN bot and a rule-based bot as an opponent.
     * 2. Runs the game simulation without collecting statistics.
     */
    @Override
    public void run() {
        for (int i = 0; i < episodes; i++) {
            Headless gameSimulation = new Headless(1, dqnBot, new RuleBasedBot("black"));

           
            gameSimulation.runGamesWithoutStats(); 

        }
    }
}
