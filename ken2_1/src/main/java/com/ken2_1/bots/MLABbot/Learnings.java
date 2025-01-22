package com.ken2_1.bots.MLABbot;

import com.ken2_1.Game_Components.Board.Ring;
import com.ken2_1.Game_Components.Board.Vertex;
import com.ken2_1.engine.Direction;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

import java.io.*;
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
    private final String rewardsFilePath = "ken2_1/src/main/java/com/ken2_1/bots/MLABbot/savedrewards.csv";
    private String botcolour;

    /**
     * Constructor initializes the rewards for each direction with an equal starting value.
     */
    public Learnings(String color) {
        botcolour = color;

        // Initialize rewards equally for all directions

        rewards = readRewardsFromCSV();
        // System.out.println("///////////////////////////////////////////////THE REWARDS HAS BEEN CREATED");
    }


    /**
     * Evaluate all moves in each direction and adjust rewards based on their performance.
     *
     * @param   AllpossibleMovesinput A map of vertex to their respective lists of moves.
     * @return
     */
    public void evaluateAndAdjust(HashMap<Vertex, ArrayList<Move>> AllpossibleMovesinput, GameState state) {

        HashMap<Direction, ArrayList<Move>> allPossibleMoves = convertVertexMoveToDirectionMove(AllpossibleMovesinput);


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
//            float newReward = Math.max(0, Math.min(1, rewards.get(direction) + averageValue));
            float newReward = (rewards.get(direction) + averageValue)/2;

            rewards.put(direction, newReward);

            // System.out.println("Direction: " + direction + ", New Reward: " + newReward);
        }
    }

    public HashMap<Direction, ArrayList<Move>> convertVertexMoveToDirectionMove(
            HashMap<Vertex, ArrayList<Move>> vertexMove) {

        HashMap<Direction, ArrayList<Move>> directionMoveMap = new HashMap<>();

        for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
            ArrayList<Move> moves = entry.getValue();

            for (Move move : moves) {
                Direction direction = move.getDirection();

                // Initialize the list for the direction if it doesn't exist
                directionMoveMap.putIfAbsent(direction, new ArrayList<>());

                // Add the move to the corresponding direction
                directionMoveMap.get(direction).add(move);
            }
        }

        saveRewardsToCSV();
        return directionMoveMap;
    }


    /**
     * Select the best move based on the top moves in each direction and their rewards.
     * @param AllpossibleMovesinput A map of directions to their respective lists of moves.
     * @return The best move based on rewards and evaluations.
     */
    public List<Move> selectBestMove(HashMap<Vertex, ArrayList<Move>> AllpossibleMovesinput, GameState state) {

        HashMap<Direction,ArrayList<Move>> allPossibleMoves = convertVertexMoveToDirectionMove(AllpossibleMovesinput);

        // A map to store the top 3 moves for each direction.
        HashMap<Direction, ArrayList<Move>> topMovesByDirection = new HashMap<>();

        List<Move> topMoves = new ArrayList<>();

        // Loop through each direction and its list of moves.
        for (Map.Entry<Direction, ArrayList<Move>> entry : allPossibleMoves.entrySet()) {
            ArrayList<Move> moves = entry.getValue(); // Retrieve the moves in the current direction.

            // Sort moves in descending order based on their evaluation scores.
            moves.sort((m1, m2) -> Float.compare(evaluateMove(m2,state), evaluateMove(m1,state)));

            // Keep only the top 3 moves for this direction.
            topMovesByDirection.put(entry.getKey(), new ArrayList<>(moves.subList(0, Math.min(3, moves.size()))));

            // Fill the
            topMoves.addAll(moves);

        }

        Direction bestDirection = null; // To store the direction with the highest reward.
        float maxReward = Float.NEGATIVE_INFINITY; // Initialize to the lowest possible value.

        // Return null if no valid move is found.
//        return topMovesByDirection.;
        return topMoves;
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

//        System.out.println(" E V A L U A T I O N      IS ::::::::"+value);
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

    public String getRewardsAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Rewards: {");

        boolean isFirst = true;
        for (Map.Entry<Direction, Float> entry : rewards.entrySet()) {
            if (!isFirst) {
                builder.append(", ");
            }
            builder.append(entry.getKey().name())
                    .append(": ")
                    .append(String.format("%.2f", entry.getValue())); // Format to 2 decimal places
            isFirst = false;
        }

        builder.append("}");
        return builder.toString();
    }

    /**
     * Save rewards to a CSV file.
     * If the file exists, average the values and update the file.
     */
    public void saveRewardsToCSV() {
        File file = new File(rewardsFilePath);

        // If the file exists, load and average the values.
        if (file.exists()) {
            HashMap<Direction, Float> existingRewards = readRewardsFromCSV();
            for (Direction direction : rewards.keySet()) {
                float newValue = (existingRewards.getOrDefault(direction, 0f) + rewards.get(direction)) / 2;
                rewards.put(direction, newValue);
            }
        }

        // Write the updated rewards to the file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rewardsFilePath))) {
            writer.write("Direction,Reward\n"); // Header
            for (Map.Entry<Direction, Float> entry : rewards.entrySet()) {
                writer.write(entry.getKey().name() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load rewards from the CSV file and update the rewards map.
     */
    private void loadRewardsFromCSV() {
        File file = new File(rewardsFilePath);

        if (!file.exists()) {
            // System.out.println("No existing rewards file found. Using default values.");
            return;
        }

        HashMap<Direction, Float> loadedRewards = readRewardsFromCSV();
        for (Direction direction : rewards.keySet()) {
            if (loadedRewards.containsKey(direction)) {
                rewards.put(direction, loadedRewards.get(direction));
            }
        }

        // System.out.println("Loaded rewards from CSV: " + getRewardsAsString());
    }

    /**
     * Read rewards from the CSV file into a HashMap.
     *
     * @return A map of directions to their rewards.
     */
    private HashMap<Direction, Float> readRewardsFromCSV() {
        HashMap<Direction, Float> loadedRewards = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rewardsFilePath))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    Direction direction = Direction.valueOf(parts[0]);
                    float reward = Float.parseFloat(parts[1]);
                    loadedRewards.put(direction, reward);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return loadedRewards;
    }




}
