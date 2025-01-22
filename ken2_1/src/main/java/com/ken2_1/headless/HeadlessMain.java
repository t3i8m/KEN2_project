package com.ken2_1.headless;

import com.ken2_1.bots.Bot;
import com.ken2_1.utils.BotFactory;

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
