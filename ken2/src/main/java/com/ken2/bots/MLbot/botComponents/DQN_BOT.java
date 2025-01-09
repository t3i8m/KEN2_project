package com.ken2.bots.MLbot.botComponents;

import com.ken2.bots.BotAbstract;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class DQN_BOT  extends BotAbstract{
    
    public DQN_BOT(String color){
        super(color);
    }

    // TODO: implement bot makeMove, uses nn to find the best move in the given state
    public Move makeMove(GameState state){
        return new Move(0, 0, null);
    }

}
