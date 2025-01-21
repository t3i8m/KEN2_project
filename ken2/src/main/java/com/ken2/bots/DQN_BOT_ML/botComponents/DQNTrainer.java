package com.ken2.bots.DQN_BOT_ML.botComponents;


import com.ken2.bots.Bot;
import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.DQN_BOT_ML.utils.ExperimentalConfig;
import com.ken2.bots.DQN_BOT_ML.utils.RewardLogger;
import com.ken2.bots.DQN_BOT_ML.utils.RollingAverageLogger;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.headless.Headless;
import com.ken2.engine.GameState;
import com.ken2.engine.GameEngine;
import com.ken2.engine.Move;

import java.util.ArrayList;

public class DQNTrainer {
    private int configNumber;
    private DQN_BOT dqnBot;
    private int episodes;  
    private int maxMovesPerEpisode; 
    private double epsilonDecay; 
    private String opponentBotType; 

    public DQNTrainer(DQN_BOT dqnBot, int episodes, int maxMovesPerEpisode, double epsilonDecay, String opponentBotType, int configNumber) {
        this.dqnBot = dqnBot;
        this.episodes = episodes;
        this.maxMovesPerEpisode = maxMovesPerEpisode;
        this.epsilonDecay = epsilonDecay;
        this.opponentBotType = opponentBotType;
        this.configNumber = configNumber;
    }

    public void train() {
        int totalWhiteWins = 0;
        int totalBlackWins = 0;
        int totalDraws = 0;
        int totalInvalidGames = 0;
        int windowSize = 10;
        double sum;
        double rollingAverage;
        ArrayList<Double> episodeFinalRewards = new ArrayList<>();
        Bot opponentBotA = initializeOpponentBot(opponentBotType);
        RewardLogger cumulativeRewardLogger = new RewardLogger("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\results\\cumulative_reward" + configNumber + ".csv");
        RollingAverageLogger rollingAverageLogger = new RollingAverageLogger("ken2\\src\\main\\java\\com\\ken2\\bots\\DQN_BOT_ML\\results\\rolling_average_reward" + configNumber + ".csv");

        for (int episode = 1; episode <= episodes; episode++) {
            cumulativeRewardLogger.resetEpisode();
            // System.out.println("Starting Episode " + episode);

            Bot opponentBot = initializeOpponentBot(opponentBotType);

            Headless gameSimulation = new Headless(1, dqnBot, opponentBot);

            gameSimulation.runGamesWithoutStats();

            dqnBot.train(30);
            double cumulativeReward = dqnBot.getCumulativeReward();
            episodeFinalRewards.add(dqnBot.getCumulativeReward());
            cumulativeRewardLogger.addReward(cumulativeReward);
            cumulativeRewardLogger.logEpisode();
            dqnBot.epsilon = Math.max(dqnBot.epsilon * epsilonDecay, dqnBot.epsilonMin);

            sum = 0;
            if (windowSize <= episode) {
                // Formula for rolling average is (1/window size)*sum of final cumulative rewards from j=current episode index - window size + 1 to current episode index
                for (int j = episode - windowSize + 1; j<=episode; j++) {
                    sum += episodeFinalRewards.get(j);
                    rollingAverage = sum/windowSize;
                    rollingAverageLogger.addAverage(rollingAverage);
                }
            } else {
                for (int k = 0; k < episode; k++) {
                    sum += episodeFinalRewards.get(k);
                    rollingAverage = sum/episode;
                    rollingAverageLogger.addAverage(rollingAverage);
                }
            }
            rollingAverageLogger.logEpisode();

            // System.out.println("Episode " + episode + " completed. Epsilon: " + dqnBot.epsilon);
            totalWhiteWins += gameSimulation.getWhiteWins();
            totalBlackWins += gameSimulation.getBlackWins();
            totalDraws += gameSimulation.getDraws();
            totalInvalidGames += gameSimulation.getInvalidGames();
        }
        cumulativeRewardLogger.close();
        rollingAverageLogger.close();



        System.out.println("=========================================");
        System.out.println("Training completed!");
        System.out.println("Total Episodes: " + episodes);
        System.out.println("White Wins (DQN Bot): " + totalWhiteWins);
        System.out.println("Black Wins ("+opponentBotA.getName()+"): " + totalBlackWins);
        System.out.println("Draws: " + totalDraws);
        System.out.println("Invalid Games: " + totalInvalidGames);
        System.out.println("=========================================");
        System.out.println("Training completed!");
        dqnBot.getQNetwork().saveWeights();
        dqnBot.saveEpsilon();
    }

    private Bot initializeOpponentBot(String type) {
        switch (type.toLowerCase()) {
            case "alphabeta":
                return new AlphaBetaBot("black");
            case "random":
            default:
                return new RuleBasedBot("black"); 
        }
    }
}
