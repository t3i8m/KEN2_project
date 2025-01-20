package com.ken2.headless;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.DQN_BOT_ML.utils.Reward;
import com.ken2.bots.DQN_BOT_ML.utils.Rewards;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.utils.BotFactory;

public class HeadlessMain {
    public static void main(String[] args){
        BotFactory factory = new BotFactory();
<<<<<<< Updated upstream
        Bot whiteBot = factory.getBot("rulebased bot", "White");
        Bot blackBot = factory.getBot("dqn bot", "Black");
=======
        Bot whiteBot = factory.getBot("alphabetaold bot", "White");
        Bot blackBot = factory.getBot("alphabeta bot", "Black");
>>>>>>> Stashed changes

        Headless headless = new Headless(100, whiteBot, blackBot);

        headless.runGames(); // without csv export
        // headless.exportGamesToCsv();// with csv export
        // headless.runGamesWithoutStats();
        int totalWhiteWins = 0;
        int totalBlackWins = 0;
        int totalDraws = 0;
        int totalInvalidGames = 0;
        totalWhiteWins += headless.getWhiteWins();
        totalBlackWins += headless.getBlackWins();
        totalDraws += headless.getDraws();
        totalInvalidGames += headless.getInvalidGames();

        // System.out.println("=========================================");
        // System.out.println("Training completed!");
        // System.out.println("White Wins ("+whiteBot.getName()+"): " + totalWhiteWins);
        // System.out.println("Black Wins ("+blackBot.getName()+"): " + totalBlackWins);
        // System.out.println("Draws: " + totalDraws);
        // System.out.println("Invalid Games: " + totalInvalidGames);
        // System.out.println("=========================================");
        // System.out.println("Training completed!");
    }
}
