package com.ken2_1.bots.AlphaBetaBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ken2_1.Game_Components.Board.*;
import com.ken2_1.bots.Bot;
import com.ken2_1.bots.BotAbstract;
import com.ken2_1.bots.DQN_BOT_ML.utils.Reward;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

/**
 * Implementation of the Alpha-Beta pruning algorithm for making optimal game moves.
 * The bot evaluates possible moves, selecting the one with the best outcome.
 */
public class AlphaBetaBot extends BotAbstract {

    private GameState StateRightNow; 
    private int chipsToRemove; 
    private List<Integer> winningChips = new ArrayList<>(); 
    private boolean switched = false; 

    /**
     * Constructor to initialize the bot with its color.
     *
     * @param color The color of the bot ("white" or "black").
     */
    public AlphaBetaBot(String color) {
        super(color); // Call the constructor of the abstract base class
    }

    /**
     * Determines the next move for the bot based on the current game state.
     *
     * @param state The current game state.
     * @return The selected move, or null if no moves are possible.
     */
    public Move makeMove(GameState state) {
        this.StateRightNow = state.clone(); 
        double alpha = Double.NEGATIVE_INFINITY; 
        double beta = Double.POSITIVE_INFINITY; 
        GameEngine ge = new GameEngine(); 
        Game_Board board = state.getGameBoard(); 
        Random random = new Random(); 

        // Get all unoccupied positions on the board
        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
            return null;
        }

        // If less than 10 rings are placed, place a new ring randomly
        if (state.ringsPlaced < 10) {
            Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size())); 
            int vertexNumber = targetVertex.getVertextNumber(); 
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(super.getColor()); 
            board.updateBoard(vertexNumber, ring); 
            state.ringsPlaced++; 
            return new Move(targetPosition[0], targetPosition[1], null); 
        }

        // Set the bot as the current player
        StateRightNow.setCurrentPlayer(super.getColor().toLowerCase());

        // Perform Alpha-Beta pruning to find the optimal move
        AlphaBetaResult result = alphaBeta(this.StateRightNow, this.StateRightNow.clone(), alpha, beta, 1, ge, null);

        // Return the best move, or null if no moves are found
        return result != null ? result.getMove() : null;
    }

    /**
     * Implements the Alpha-Beta pruning algorithm to evaluate the best move.
     *
     * @param state      The current game state.
     * @param prevState  The previous game state.
     * @param alpha      The alpha value for pruning.
     * @param beta       The beta value for pruning.
     * @param ge         The game engine instance.
     * @param currentMove The move currently being evaluated.
     * @return The result of the evaluation, including the best move and its value.
     */
    public AlphaBetaResult alphaBeta(GameState state, GameState prevState, double alpha, double beta, int limitVar, GameEngine ge, Move currentMove) {
        GameEngine ge2 = new GameEngine(); 

        if (limitVar == 0) {
            double evaluation = evaluate(state, prevState, ge, prevState.getCurrentColor(), currentMove); 
            return new AlphaBetaResult(evaluation, null); 
        }

        // Determine whether the current player is maximizing or minimizing
        boolean isMaximizingPlayer = state.getCurrentColor().equalsIgnoreCase(super.getColor());
        double value = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; 
        Move bestMove = null; // Track the best move found

        // Get all valid moves for the current player's rings
        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.getCurrentColor().toLowerCase());
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge2.getAllMovesFromAllPositions(allRingPositions, state.getGameBoard());
        HashMap<Vertex, ArrayList<Move>> validMoves = filterValidMoves(vertexMove, state); // Filter valid moves

        // Sort moves by heuristic evaluation for optimization
        List<Move> sortedMoves = sortMoves(validMoves, state, prevState, ge);

        // If no valid moves exist, return the evaluation of the current state
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves at depth=" + limitVar);
            return new AlphaBetaResult(evaluate(state, prevState, ge, prevState.getCurrentColor().toLowerCase(), currentMove), null);
        }

        // Iterate through all sorted moves
        for (Move m : sortedMoves) {
            // Apply the move to a new state
            GameState newState = moveState(state.clone(), m);

            // Switch the player if needed
            if (!switched) {
                newState.switchPlayerNEW();
            }

            // Calculate the reward for the move
            double reward = Reward.calculateRewardWITHOUT(ge, state.clone(), m, newState.clone(), state.getCurrentColor().toLowerCase());
            m.setReward(reward);

            // Recursively evaluate the resulting state
            AlphaBetaResult result = alphaBeta(newState, state, alpha, beta, limitVar - 1, ge, m);
            double currentValue = result.getValue(); // Extract the evaluation value

            // Update alpha/beta bounds and track the best move
            if (isMaximizingPlayer) {
                if (currentValue > value) {
                    value = currentValue;
                    bestMove = m; // Update best move for maximizing player
                }
                alpha = Math.max(alpha, value); // Update alpha
                if (alpha >= beta) {
                    break; // Prune branch
                }
            } else {
                if (currentValue < value) {
                    value = currentValue;
                    bestMove = m; // Update best move for minimizing player
                }
                beta = Math.min(beta, value); // Update beta
                if (alpha >= beta) {
                    break; // Prune branch
                }
            }
        }

        // Return the evaluation value and the best move found
        return new AlphaBetaResult(value, bestMove);
    }

    /**
     * Sorts moves based on their heuristic evaluation value.
     * Moves with higher evaluation values are prioritized.
     *
     * @param validMoves A map of vertices and their associated valid moves.
     * @param state      The current game state.
     * @param prevState  The previous game state.
     * @param ge         The game engine instance.
     * @return A list of moves sorted by their evaluation value in descending order.
     */
    private List<Move> sortMoves(HashMap<Vertex, ArrayList<Move>> validMoves, GameState state, GameState prevState, GameEngine ge) {
        List<Move> allMoves = new ArrayList<>(); 

        for (ArrayList<Move> moves : validMoves.values()) {
            allMoves.addAll(moves);
        }

        allMoves.sort((Move m1, Move m2) -> {
            double value1 = evaluate(moveState(state, m1), prevState, ge, state.getCurrentColor(), m1); 
            double value2 = evaluate(moveState(state, m2), prevState, ge, state.getCurrentColor(), m2); 
            return Double.compare(value2, value1); 
        });

        return allMoves; 
    }
    

    /**
     * Filters valid moves from a map of possible moves for all vertices.
     * Ensures that only legal and valid moves are considered.
     *
     * @param vertexMove A map of vertices and their associated moves.
     * @param state      The current game state.
     * @return A map of vertices and their filtered valid moves.
     */
    private HashMap<Vertex, ArrayList<Move>> filterValidMoves(HashMap<Vertex, ArrayList<Move>> vertexMove, GameState state) {
        HashMap<Vertex, ArrayList<Move>> validMoves = new HashMap<>(); 

        for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
            if (entry == null || entry.getValue() == null) {
                continue; 
            }

            ArrayList<Move> validMovesForVertex = new ArrayList<>(); 

            for (Move m : entry.getValue()) {
                if (isValidMove(state, m)) { 
                    validMovesForVertex.add(m); 
                }
            }

            if (!validMovesForVertex.isEmpty()) {
                validMovesForVertex.sort((Move m1, Move m2) -> {
                    double value1 = evaluate(moveState(state, m1), state, new GameEngine(), state.getCurrentColor(), m1);
                    double value2 = evaluate(moveState(state, m2), state, new GameEngine(), state.getCurrentColor(), m2);
                    return Double.compare(value2, value1); 
                });

                ArrayList<Move> topMoves = new ArrayList<>(validMovesForVertex.subList(0, Math.min(1, validMovesForVertex.size())));
                validMoves.put(entry.getKey(), topMoves);
            }
        }

        return validMoves; 
    }
    
    

     /**
     * Evaluates a given game state and returns a numerical score.
     *
     * @param state       The game state to evaluate.
     * @param prevState   The previous game state for comparison.
     * @param ge          The game engine instance.
     * @param color       The color of the player being evaluated.
     * @param chosenMove  The move that led to this state.
     * @return A numerical value representing the desirability of the state.
     */
    private double evaluate(GameState state, GameState prevState, GameEngine ge, String color, Move chosenMove) {
        if (state == null || prevState == null || chosenMove == null) {
            System.out.println("Invalid inputs to evaluate: state=" + state + ", prevState=" + prevState + ", chosenMove=" + chosenMove);
            return Double.NEGATIVE_INFINITY; 
        }

        double valuation = Reward.calculateRewardWITHOUT(ge, prevState, chosenMove, state, color);

        if (Double.isInfinite(valuation) || Double.isNaN(valuation)) {
            System.out.println("Error in evaluation: value is invalid. Returning fallback value.");
            return 0; 
        }

        return valuation; 
    }

    /**
     * Creates a new game state by applying a move to an existing state.
     *
     * @param state The original game state.
     * @param move  The move to apply.
     * @return The resulting game state after applying the move.
     */
    private GameState moveState(GameState state, Move move) {
            GameState newState = state.clone();
            
            Game_Board board = newState.getGameBoard();

            String thisBotColor = super.getColor().toLowerCase().equals("white")? "white":"black";
            Bot thisBot = new AlphaBetaBot(thisBotColor);
            Bot opponentBot = new AlphaBetaBot(thisBotColor.toLowerCase().equals("white")?"black":"white");

            String currentPlayerColor="";
        
            currentPlayerColor = state.currentPlayerColor().toLowerCase().equals("white") ? "white" : "black";

            GameEngine tempEngine = new GameEngine();
            tempEngine.currentState = newState.clone();
        
            Vertex fromVertex = move.getStartingVertex();
        
            if (fromVertex == null) {
                System.out.println("Error: Source vertex is null.");
                return state; 
            }

            int fromIndex = fromVertex.getVertextNumber();
            int toIndex = board.getVertexNumberFromPosition(
                    move.getXposition(),
                    move.getYposition()
            );
        
            if (toIndex == -1) {
                System.out.println("Error: Invalid target vertex index.");
                return state;
            }

            tempEngine.placeChip(fromIndex);
            Move currentMove = tempEngine.gameSimulation.simulateMove(
                board,
                tempEngine.currentState.gameBoard.getVertex(fromIndex),
                tempEngine.currentState.gameBoard.getVertex(toIndex)
            );

            Vertex sourceV = tempEngine.currentState.gameBoard.getVertex(fromIndex);

        
            if (sourceV == null) {
                System.out.println("sourceV is null. : ");

                return state;
            }

            Ring ringToMove;
            if (sourceV.hasRing()) {
                ringToMove = (Ring) sourceV.getRing();
                sourceV.setRing(null);
            } else {
                System.out.println("hasRing is null. CurrentBot: " );

                return state;
            }

        
            List<Coin> flippedCoins = currentMove.getFlippedCoins();
            ArrayList<Vertex> verticesToFlip = new ArrayList<>();
            if (flippedCoins != null && !flippedCoins.isEmpty()) {
                for (Coin coin : flippedCoins) {
                    Vertex coinVert = tempEngine.currentState.gameBoard.getVertex(coin.getVertex());
                    if (coinVert == null) {
                        Vertex newVert = tempEngine.currentState.gameBoard.getVertex(coin.getVertex());
                        if (!verticesToFlip.contains(newVert)) {
                            verticesToFlip.add(newVert);
                        }
                        continue;
                    }
                    if (!verticesToFlip.contains(coinVert)) {
                        verticesToFlip.add(coinVert);
                    }
                }
            }

            tempEngine.gameSimulation.flipCoinsByVertex(verticesToFlip, tempEngine.currentState.gameBoard);
            Vertex targetV = tempEngine.currentState.gameBoard.getVertex(toIndex);

            if (targetV == null || targetV.hasRing()) {
                System.out.println("targetV is null. CurrentBot: ");

                return state;
            }
            targetV.setRing(ringToMove);

        
            tempEngine.currentState.chipsRemaining--;
            tempEngine.currentState.chipRingVertex = -1;
            tempEngine.currentState.chipPlaced = false;
            tempEngine.currentState.selectedRingVertex = -1;
            tempEngine.currentState.updateChipsRingCountForEach();
            tempEngine.currentState.setVertexesOfFlippedCoins(verticesToFlip);
            if (!verticesToFlip.contains(sourceV)) {
                verticesToFlip.add(sourceV);
            }
            boolean isWinningRow = tempEngine.win(tempEngine.currentState.getVertexesOfFlippedCoins());

            switched = false;

            if (isWinningRow) {
                String winnerColor = tempEngine.getWinningColor();
                Bot currentPlayer = winnerColor.equalsIgnoreCase(super.getColor()) ? thisBot : opponentBot;

                Vertex vertexToRemoveBOT = currentPlayer.removeRing(tempEngine.currentState);
                String vertexColor = vertexToRemoveBOT.getRing().getColour();
                handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);

                ArrayList<Integer> allRemoveChips = currentPlayer.removeChips(tempEngine.currentState);
                for (Integer vert : allRemoveChips) {
                    handleChipRemove(vert, tempEngine, currentPlayer);
                }
                tempEngine.setWinningRing(false);
                tempEngine.setChipRemovalMode(false);
                tempEngine.currentState.setVertexesOfFlippedCoins(null);

                tempEngine.currentState.updateChipsRingCountForEach();
                tempEngine.setChipRemovalMode(false);
                tempEngine.setRingSelectionMode(false);
                tempEngine.setWinningColor("");
                winningChips.clear();
                tempEngine.setWinningRing(false);
                tempEngine.setChipRemovalMode(false);
                tempEngine.getWinningChips().clear();

            }
        
            return tempEngine.currentState;
        }

    /**
     * Handles the removal of a winning ring from the board.
     * This method is called when a winning pattern is detected.
     *
     * @param vertex      The vertex where the winning ring is located.
     * @param gameEngine  The game engine managing the current state.
     */
    public void handleWinningRing(int vertex, GameEngine gameEngine) {
        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v != null
            && v.hasRing()
            && v.getRing().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {

            v.setRing(null);
            gameEngine.findAndSetAllWinningChips(gameEngine.getWinningColor());
            gameEngine.currentState.setAllPossibleCoinsToRemove(gameEngine.getWinningChips());
            gameEngine.setRingSelectionMode(false);
            gameEngine.setChipRemovalMode(true);
            chipsToRemove = 5;
        }
    }

    /**
     * Handles the removal of a chip from the board as part of a winning sequence.
     * Updates the game state and performs cleanup after the chip is removed.
     *
     * @param vertex      The vertex where the chip to be removed is located.
     * @param gameEngine  The game engine managing the current state.
     * @param activeBot   The bot performing the chip removal.
     */
    public void handleChipRemove(int vertex, GameEngine gameEngine, Bot activeBot) {
        if (!gameEngine.getWinningChips().contains(vertex)) {
            return;
        }

        Vertex v = gameEngine.currentState.gameBoard.getVertex(vertex);
        if (v == null || !v.hasCoin()) {
            return;
        }

        String currColor = v.getCoin().getColour().toLowerCase();
        if (currColor.equalsIgnoreCase(gameEngine.getWinningColor().toLowerCase())) {
            v.setPlayObject(null);
            gameEngine.currentState.chipsRemaining += 1;
            chipsToRemove--;
            gameEngine.getWinningChips().remove(Integer.valueOf(vertex));

            List<Integer> adjacentVertices = gameEngine.getAdjacentVertices(vertex);
            List<Integer> validRemovableChips = new ArrayList<>();
            for (int adjVertex : adjacentVertices) {
                if (winningChips.contains(adjVertex)) {
                    Vertex adjV = gameEngine.currentState.gameBoard.getVertex(adjVertex);
                    if (adjV != null && adjV.hasCoin()
                            && adjV.getCoin().getColour().equalsIgnoreCase(gameEngine.getWinningColor())) {
                        validRemovableChips.add(adjVertex);
                    }
                }
            }

            if (chipsToRemove <= 0) {
                gameEngine.setChipRemovalMode(false);
                gameEngine.setRingSelectionMode(false);
                gameEngine.setWinningColor("");
                winningChips.clear();
                gameEngine.getWinningChips().clear();

                if (currColor.toLowerCase().equals(activeBot.getColor().toLowerCase())) {

                }
            }
        }
    }



    /**
     * Validates whether a move is legal according to the game rules.
     *
     * @param state The current game state.
     * @param move  The move to validate.
     * @return True if the move is valid, false otherwise.
     */
    private boolean isValidMove(GameState state, Move move) {
        Vertex[][] board = state.getGameBoard().getBoard();
        Vertex startVertex = move.getStartingVertex();
    
        if (startVertex == null || !startVertex.hasRing()) {

            return false;
        }
    
        PlayObj ring = (Ring)startVertex.getRing();
        if (!ring.getColour().toLowerCase().equals(state.getCurrentColor().toLowerCase())) {
            
            return false;
        }
    
        int startX = startVertex.getXposition();
        int startY = startVertex.getYposition();
        int targetX = move.getXposition();
        int targetY = move.getYposition();
    
        com.ken2_1.engine.Direction direction = move.getDirection();
    
        if (direction == null) {

            return false;
        }
    
        int deltaX = direction.getDeltaX();
        int deltaY = direction.getDeltaY();
    
        if (deltaX == 0 && deltaY == 0) {

            return false;
        }
    
        int dx = targetX - startX;
        int dy = targetY - startY;
    
        if (deltaX == 0) {
            if (dx != 0) {
                return false; 
            }
            if (deltaY == 0 || dy % deltaY != 0 || (dy / deltaY) <= 0) {
                return false; 
            }
        } else if (deltaY == 0) {
            if (dy != 0) {
                return false; 
            }
            if (deltaX == 0 || dx % deltaX != 0 || (dx / deltaX) <= 0) {
                return false; 
            }
        } else {
            if (dx % deltaX != 0 || dy % deltaY != 0) {
                return false; 
            }
            int stepsX = dx / deltaX;
            int stepsY = dy / deltaY;
            if (stepsX != stepsY || stepsX <= 0) {
                return false; 
            }
        }
    
        int currentX = startX;
        int currentY = startY;
    
        while (true) {
            currentX += deltaX;
            currentY += deltaY;
    
            if (currentX == targetX && currentY == targetY) {
                break;
            }
    
            if (currentX < 0 || currentX >= board.length || currentY < 0 || currentY >= board[0].length) {

                return false; 
            }
    
            Vertex currentVertex = board[currentX][currentY];
    
            if (currentVertex != null && currentVertex.hasRing()) {

                return false; 
            }
    
        }
    
        Vertex targetVertex = board[targetX][targetY];
    
        if (targetVertex == null || targetVertex.hasRing() || targetVertex.hasCoin()) {

            return false; 
        }
    
        return true;
    }
    /**
     * Returns the bot's name.
     *
     * @return The bot's name as a string.
     */
    public String getName() {
        return "AlphaBeta";
    }
    
    
}
