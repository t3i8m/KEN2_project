package com.ken2.bots.MLAlphaBetabot;

import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Direction;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages rewards for different directions in a board game.
 * It evaluates moves, adjusts rewards dynamically, and selects the best move based on weighted directions.
 */
public class Learnings {

    // A HashMap to store rewards associated with each direction.
    private final HashMap<Direction, Float> rewards;

    private String botcolour;

    /**
     * Constructor initializes the rewards for each direction with an equal starting value.
     */
    public Learnings(String color) {
        rewards = new HashMap<>();
        botcolour = color;

        // Initialize rewards equally for all directions.
        rewards.put(Direction.UP, 0.16f);
        rewards.put(Direction.DOWN, 0.16f);
        rewards.put(Direction.LEFT_UP, 0.16f);
        rewards.put(Direction.LEFT_DOWN, 0.16f);
        rewards.put(Direction.RIGHT_UP, 0.16f);
        rewards.put(Direction.RIGHT_DOWN, 0.16f);

        System.out.println("///////////////////////////////////////////////THE REWARDS HAS BEEN CREATED");
    }


    /**
     * Evaluate all moves in each direction and adjust rewards based on their performance.
     *
     * @param allPossibleMoves A map of directions to their respective lists of moves.
     * @return
     */
    public void evaluateAndAdjust(HashMap<Direction, ArrayList<Move>> allPossibleMoves, GameState state) {
        for (Map.Entry<Direction, ArrayList<Move>> entry : allPossibleMoves.entrySet()) {
            Direction direction = entry.getKey();
            List<Move> moves = entry.getValue();

            if (moves.isEmpty()) continue; // Skip empty directions.

            float totalValue = 0;

            for (Move move : moves) {
                float moveValue = evaluateMove(move, state);
                if (moveValue == 0) continue; // Skip invalid moves.
                totalValue += moveValue;
            }

            float averageValue = moves.size() > 0 ? totalValue / moves.size() : 0;
            float newReward = Math.max(0, Math.min(1, rewards.get(direction) + averageValue));
            rewards.put(direction, newReward);

            System.out.println("Direction: " + direction + ", New Reward: " + newReward);
        }
    }

    /**
     * Select the best move based on the top moves in each direction and their rewards.
     * @param allPossibleMoves A map of directions to their respective lists of moves.
     * @return The best move based on rewards and evaluations.
     */
    public void selectBestMove(HashMap<Direction, ArrayList<Move>> allPossibleMoves, GameState state) {
        // A map to store the top 3 moves for each direction.
        HashMap<Direction, ArrayList<Move>> topMovesByDirection = new HashMap<>();

        // Loop through each direction and its list of moves.
        for (Map.Entry<Direction, ArrayList<Move>> entry : allPossibleMoves.entrySet()) {
            ArrayList<Move> moves = entry.getValue(); // Retrieve the moves in the current direction.

            // Sort moves in descending order based on their evaluation scores.
            moves.sort((m1, m2) -> Float.compare(evaluateMove(m2,state), evaluateMove(m1,state)));

            // Keep only the top 3 moves for this direction.
            topMovesByDirection.put(entry.getKey(), new ArrayList<>(moves.subList(0, Math.min(3, moves.size()))));

        }

        Direction bestDirection = null; // To store the direction with the highest reward.
        float maxReward = Float.NEGATIVE_INFINITY; // Initialize to the lowest possible value.

        // Return null if no valid move is found.
//        return topMovesByDirection;
    }

    /**
     *Evaluate the quality of a move
     * @param move The move to evaluate
     * @param state state to apply the move
     * @return float of the evaulation
     */
    private float evaluateMove(Move move, GameState state){
        float value = 0;
        GameEngine ge = new GameEngine();

        double initval = evaluate(state,ge,botcolour);
        GameState newstate = moveState(state,move);
        double finval = evaluate(newstate,ge,botcolour);

        value = ((float) initval + (float) finval)/2;

        System.out.println(" E V A L U A T I O N      IS ::::::::"+value);
        return value;
    }

    /**
     * Get the current reward values for all directions.
     * @return A map of directions and their respective rewards.
     */
    public HashMap<Direction, Float> getRewards() {
        return rewards;
    }

    private double evaluate(GameState state, GameEngine ge, String color) {
        double valuation = 0;
        String opponentColor = color.equals("white") ? "black" : "white";

        double inOurfavour = 0.5;
        double notOurfavour = -0.25;
        double ourWin = 1;
        double theirWin = -1;

        valuation += state.getChipsCountForColor(color) * inOurfavour
                + state.getChipsCountForColor(opponentColor) * notOurfavour;

        valuation += state.getRingCountForColor(color) * inOurfavour
                + state.getRingCountForColor(opponentColor) * notOurfavour;

        valuation += ge.winningColor(state.getVertexesOfFlippedCoins()).equals(color) ? ourWin : theirWin;

        return valuation;
    }

    private GameState moveState(GameState state, Move move) {
        GameState newState = state.clone();

        GameEngine tempEngine = new GameEngine();
        tempEngine.currentState = newState;

        int toVertex = tempEngine.gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
        if (toVertex == -1) return state;

        Vertex sourceVertex = move.getStartingVertex();
        Vertex targetVertex = tempEngine.currentState.gameBoard.getVertex(toVertex);
        if (targetVertex == null || targetVertex.hasRing() || targetVertex.hasCoin()) return state;
        if (sourceVertex == null || !sourceVertex.hasRing()) return state;


        Ring ringToMove = (Ring) sourceVertex.getRing();
        if (ringToMove != null) {
            sourceVertex.setRing(null);
            tempEngine.gameBoard.updateBoard(toVertex, ringToMove);
        } else {
            return state;
        }

        return tempEngine.currentState;
    }
}
