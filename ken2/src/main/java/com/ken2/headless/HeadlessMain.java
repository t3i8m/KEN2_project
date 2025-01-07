package com.ken2.headless;

import com.ken2.bots.Bot;
import com.ken2.utils.BotFactory;

public class HeadlessMain {
    public static void main(String[] args){
        BotFactory factory = new BotFactory();
        Bot whiteBot = factory.getBot("rulebased bot", "White");
        Bot blackBot = factory.getBot("rulebased bot", "Black");

        Headless headless = new Headless(1000, whiteBot, blackBot);
        // headless.runGames(); // without csv export
        headless.exportGamesToCsv();// with csv export
    }
}
