package com.ken2.utils;

import com.ken2.bots.Bot;
import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.RuleBased.RuleBasedBot;

public class BotFactory {

    /**
     * factory that returns a bot by a given name and color
     * @param botType name of the bot {random bot, ...}
     * @param botColor color of the bot
     * @return bot obj or null
     */
    public Bot getBot(String botType, String botColor){
        if(botType==null && botColor == null){
            return null;
        }
        switch(botType.toLowerCase()){
            case "rulebased bot":
                return new RuleBasedBot(botColor);
            case "alphabeta bot":
                return new AlphaBetaBot(botColor);
        }
        return null;
    }
}