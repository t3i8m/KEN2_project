package com.ken2_1.bots.AlphaBetaBot;

import com.ken2_1.engine.Move;

/**
 * Represents the result of the Alpha-Beta pruning algorithm.
 * Stores the evaluation value and the best move found during the search.
 */
public class AlphaBetaResult {
    private double value = 0;
    private Move bestMove;


    /**
     * Constructor to initialize the AlphaBetaResult with a specific value and move.
     *
     * @param value    The evaluation value associated with the result.
     * @param bestMove The best move determined by the Alpha-Beta algorithm.
     */
    public AlphaBetaResult(double value, Move bestMove){
        this.value = value;
        this.bestMove = bestMove;
    }

    /**
     * Returns the evaluation value for the result.
     *
     * @return The evaluation value.
     */
    public double getValue(){
        return this.value;
    }
    
     /**
     * Returns the best move determined by the Alpha-Beta pruning algorithm.
     *
     * @return The best move.
     */
    public Move getMove(){
        return this.bestMove;
    }
}
