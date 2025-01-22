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
        Bot whiteBot = factory.getBot("dqn bot", "White");
        Bot blackBot = factory.getBot("rulebased bot", "Black");

        Headless headless = new Headless(100, whiteBot, blackBot);

        headless.runGames(); // without csv export
        int totalWhiteWins = 0;
        int totalBlackWins = 0;
        int totalDraws = 0;
        int totalInvalidGames = 0;
        totalWhiteWins += headless.getWhiteWins();
        totalBlackWins += headless.getBlackWins();
        totalDraws += headless.getDraws();
        totalInvalidGames += headless.getInvalidGames();

    }
}
