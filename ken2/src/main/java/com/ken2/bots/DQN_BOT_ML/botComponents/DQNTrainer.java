package com.ken2.bots.DQN_BOT_ML.botComponents;


import com.ken2.bots.Bot;
import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.headless.Headless;



/**
 * Class for training the DQN-based bot using self-play or play against predefined opponents.
 * Handles training over multiple episodes and adjusts exploration over time.
 */
public class DQNTrainer {
    private DQN_BOT dqnBot;
    private int episodes;  
    private int maxMovesPerEpisode; 
    private double epsilonDecay; 
    private String opponentBotType; 


    /**
     * Constructor to initialize the trainer with the given parameters.
     *
     * @param dqnBot              The DQN-based bot to train.
     * @param episodes            Number of training episodes.
     * @param maxMovesPerEpisode  Maximum moves allowed per episode.
     * @param epsilonDecay        Decay factor for the exploration rate.
     * @param opponentBotType     Type of opponent bot to train against.
     */
    public DQNTrainer(DQN_BOT dqnBot, int episodes, int maxMovesPerEpisode, double epsilonDecay, String opponentBotType) {
        this.dqnBot = dqnBot;
        this.episodes = episodes;
        this.maxMovesPerEpisode = maxMovesPerEpisode;
        this.epsilonDecay = epsilonDecay;
        this.opponentBotType = opponentBotType; 
    }

    /**
     * Starts the training process for the DQN bot.
     * Conducts multiple episodes of games and adjusts the bot's policy based on results.
     */
    public void train() {
        int totalWhiteWins = 0;
        int totalBlackWins = 0;
        int totalDraws = 0;
        int totalInvalidGames = 0;
        Bot opponentBotA = initializeOpponentBot(opponentBotType);

        for (int episode = 1; episode <= episodes; episode++) {

            Bot opponentBot = initializeOpponentBot(opponentBotType);

            Headless gameSimulation = new Headless(1, dqnBot, opponentBot);

            gameSimulation.runGamesWithoutStats();

            dqnBot.train(30); 
            dqnBot.epsilon = Math.max(dqnBot.epsilon * epsilonDecay, dqnBot.epsilonMin);

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
        dqnBot.saveEpsilon();
    }

    /**
     * Initializes the opponent bot based on the specified type.
     *
     * @param type The type of opponent bot (e.g., "alphabeta", "random").
     * @return The initialized opponent bot.
     */
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
