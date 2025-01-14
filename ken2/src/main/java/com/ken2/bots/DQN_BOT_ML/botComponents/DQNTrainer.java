package com.ken2.bots.DQN_BOT_ML.botComponents;


import com.ken2.bots.Bot;
import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.headless.Headless;
import com.ken2.engine.GameState;
import com.ken2.engine.GameEngine;
import com.ken2.engine.Move;

public class DQNTrainer {
    private DQN_BOT dqnBot;
    private int episodes;  
    private int maxMovesPerEpisode; 
    private double epsilonDecay; 
    private String opponentBotType; 

    public DQNTrainer(DQN_BOT dqnBot, int episodes, int maxMovesPerEpisode, double epsilonDecay, String opponentBotType) {
        this.dqnBot = dqnBot;
        this.episodes = episodes;
        this.maxMovesPerEpisode = maxMovesPerEpisode;
        this.epsilonDecay = epsilonDecay;
        this.opponentBotType = opponentBotType; 
    }

    public void train() {
        int totalWhiteWins = 0;
        int totalBlackWins = 0;
        int totalDraws = 0;
        int totalInvalidGames = 0;
        Bot opponentBotA = initializeOpponentBot(opponentBotType);

        for (int episode = 1; episode <= episodes; episode++) {
            // System.out.println("Starting Episode " + episode);

            Bot opponentBot = initializeOpponentBot(opponentBotType);

            Headless gameSimulation = new Headless(1, dqnBot, opponentBot);

            gameSimulation.runGamesWithoutStats();

            dqnBot.train(30); 
            dqnBot.epsilon = Math.max(dqnBot.epsilon * epsilonDecay, dqnBot.epsilonMin);

            // System.out.println("Episode " + episode + " completed. Epsilon: " + dqnBot.epsilon);
            totalWhiteWins += gameSimulation.getWhiteWins();
            totalBlackWins += gameSimulation.getBlackWins();
            totalDraws += gameSimulation.getDraws();
            totalInvalidGames += gameSimulation.getInvalidGames();
        }
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
