package com.ken2.bots.MLAlphaBetabot;

import com.ken2.engine.Direction;
import com.ken2.engine.Move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class manages rewards for different directions in a board game.
 * It evaluates moves, adjusts rewards dynamically, and selects the best move based on weighted directions.
 */
public class Rewards {

    // A HashMap to store rewards associated with each direction.
    private final HashMap<Direction, Float> rewards;

    /**
     * Constructor initializes the rewards for each direction with an equal starting value.
     */
    public Rewards() {
        rewards = new HashMap<>();

        // Initialize rewards equally for all directions.
        rewards.put(Direction.UP, 0.16f);
        rewards.put(Direction.DOWN, 0.16f);
        rewards.put(Direction.LEFT_UP, 0.16f);
        rewards.put(Direction.LEFT_DOWN, 0.16f);
        rewards.put(Direction.RIGHT_UP, 0.16f);
        rewards.put(Direction.RIGHT_DOWN, 0.16f);
    }

    /**
     * Increment the reward for a specific direction by a given value.
     * @param direction The direction to adjust.
     * @param value The value to add to the reward.
     */
    public void increment(Direction direction, float value) {
        rewards.put(direction, rewards.get(direction) + value);
    }

    /**
     * Decrement the reward for a specific direction by a given value.
     * @param direction The direction to adjust.
     * @param value The value to subtract from the reward.
     */
    public void decrement(Direction direction, float value) {
        rewards.put(direction, rewards.get(direction) - value);
    }

    /**
     * Evaluate all moves in each direction and adjust rewards based on their performance.
     * @param allPossibleMoves A map of directions to their respective lists of moves.
     */
    public void evaluateAndAdjust(HashMap<Direction, List<Move>> allPossibleMoves) {
        // Loop through each entry in the map (direction and its moves).
        for (Map.Entry<Direction, List<Move>> entry : allPossibleMoves.entrySet()) {
            Direction direction = entry.getKey(); // Current direction.
            List<Move> moves = entry.getValue();  // List of moves in this direction.

            // Skip if there are no moves in this direction.
            if (moves.isEmpty()) continue;

            float totalValue = 0; // To calculate the sum of move evaluations.

            // Evaluate each move and add its score to the total.
            for (Move move : moves) {
                totalValue += evaluateMove(move);
            }

            // Calculate the average value of moves in this direction.
            float averageValue = totalValue / moves.size();

            // Adjust the reward for this direction by adding the average value.
            rewards.put(direction, rewards.get(direction) + averageValue);
        }
    }

    /**
     * Select the best move based on the top moves in each direction and their rewards.
     * @param allPossibleMoves A map of directions to their respective lists of moves.
     * @return The best move based on rewards and evaluations.
     */
    public Move selectBestMove(HashMap<Direction, List<Move>> allPossibleMoves) {
        // A map to store the top 3 moves for each direction.
        HashMap<Direction, List<Move>> topMovesByDirection = new HashMap<>();

        // Loop through each direction and its list of moves.
        for (Map.Entry<Direction, List<Move>> entry : allPossibleMoves.entrySet()) {
            List<Move> moves = entry.getValue(); // Retrieve the moves in the current direction.

            // Sort moves in descending order based on their evaluation scores.
            moves.sort((m1, m2) -> Float.compare(evaluateMove(m2), evaluateMove(m1)));

            // Keep only the top 3 moves for this direction.
            topMovesByDirection.put(entry.getKey(), moves.subList(0, Math.min(3, moves.size())));
        }

        Direction bestDirection = null; // To store the direction with the highest reward.
        float maxReward = Float.NEGATIVE_INFINITY; // Initialize to the lowest possible value.

        // Find the direction with the highest reward.
        for (Map.Entry<Direction, Float> rewardEntry : rewards.entrySet()) {
            if (rewardEntry.getValue() > maxReward) {
                maxReward = rewardEntry.getValue();
                bestDirection = rewardEntry.getKey();
            }
        }

        // If a valid direction is found, choose a random move from its top 3 moves.
        if (bestDirection != null && topMovesByDirection.containsKey(bestDirection)) {
            List<Move> topMoves = topMovesByDirection.get(bestDirection);
            return topMoves.get(new Random().nextInt(topMoves.size()));
        }

        // Return null if no valid move is found.
        return null;
    }

    /**
     * Evaluate the quality of a move.
     * @param move The move to evaluate.
     * @return A float value representing the move's quality.
     */
    private float evaluateMove(Move move) {
        // For now, return a random value as a placeholder.
        return new Random().nextFloat();
    }

    /**
     * Get the current reward values for all directions.
     * @return A map of directions and their respective rewards.
     */
    public HashMap<Direction, Float> getRewards() {
        return rewards;
    }
}
