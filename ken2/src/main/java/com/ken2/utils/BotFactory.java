package com.ken2.bots;

import com.ken2.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2.bots.RuleBased.RuleBasedBot;

public class BotFactory {

    /**
     * factory that returns a bot by a given name and color
     * @param botType name of the bot {random bot, ...}
     * @param botColor 
     * @return bot obj or null
     */
    public Bot geBot(String botType, String botColor){
        if(botType==null && botColor == null){
            return null;
        }

        switch(botType.toLowerCase()){
            case "random bot":
                return new RuleBasedBot(botColor);
            case "AlphaBeta":
                return new AlphaBetaBot(botColor);
        }
        return null;
    }
}
