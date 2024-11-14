package com.ken2.bots.AlphaBetaBot;

import com.ken2.engine.Move;

public class AlphaBetaResult {
    private double value;
    private Move bestMove;

    public AlphaBetaResult(double value, Move bestMove){
        this.value = value;
        this.bestMove = bestMove;
    }

    public double getValue(){
        return this.value;
    }

    public Move getMove(){
        return this.bestMove;
    }
}
