package com.ken2.bots.AlphaBetaBot;

import com.ken2.engine.Move;

/**
 * Class for the results from the Alpha Beta bots
 */
public class AlphaBetaResult {
    private double value = 0;
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
