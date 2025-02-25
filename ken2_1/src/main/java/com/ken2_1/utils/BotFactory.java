package com.ken2_1.utils;

import com.ken2_1.bots.Bot;
import com.ken2_1.bots.AlphaBetaBot.AlphaBetaBot;
import com.ken2_1.bots.DQN_BOT_ML.botComponents.DQN_BOT;
import com.ken2_1.bots.MLABbot.MLAlphaBetaBot;
import com.ken2_1.bots.RuleBased.RuleBasedBot;

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
            case "dqn bot":
                return new DQN_BOT(botColor);
            case "ml alpha beta":
                return new MLAlphaBetaBot(botColor);
        }
        return null;
    }
}