package com.ken2.bots.MLAlphaBetabot;

import com.ken2.engine.Move;

import java.util.List;

public class BranchResult {
    private List<Double> evaluations; // Evaluations of the moves
    private int depth; // Depth of AlphaBeta search
    private Move bestMove; // Predefined Move object representing the best move

    // Constructor
    public BranchResult(List<Double> evaluations, int depth, Move bestMove) {
        this.evaluations = evaluations;
        this.depth = depth;
        this.bestMove = bestMove;
    }

    // Getters
    public List<Double> getEvaluations() {
        return evaluations;
    }

    public int getDepth() {
        return depth;
    }

    public Move getBestMove() {
        return bestMove;
    }

    // Setters
    public void setEvaluations(List<Double> evaluations) {
        this.evaluations = evaluations;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

    @Override
    public String toString() {
        return "BranchResult{" +
                "evaluations=" + evaluations +
                ", depth=" + depth +
                ", bestMove=" + bestMove +
                '}';
    }
}
