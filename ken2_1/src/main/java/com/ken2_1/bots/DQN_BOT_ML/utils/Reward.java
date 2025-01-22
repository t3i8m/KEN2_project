package com.ken2_1.bots.DQN_BOT_ML.utils;

import java.util.HashSet;
import java.util.Set;

import com.ken2_1.Game_Components.Board.Vertex;
import com.ken2_1.engine.Direction;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

/**
 * Provides methods to calculate rewards for specific game transitions.
 * Used in reinforcement learning to train AI agents.
 */
public class Reward {

    /**
     * Calculates the total reward for a given move and state transition.
     *
     * @param engine        Game engine to evaluate the game state.
     * @param previousState The game state before the move.
     * @param move          The move performed.
     * @param newState      The game state after the move.
     * @param currentColor  The color of the current player ("white" or "black").
     * @return The normalized reward value.
     */
    public static double calculateReward(GameEngine engine, GameState previousState, Move move, GameState newState, String currentColor) {
        double reward = 0.0;

        if (isWin(engine, newState, currentColor)) {
            reward += Rewards.WIN.getValue();
            Rewards.logReward(Rewards.WIN, "VICTORY");
        } else if (isLose(engine, newState, currentColor)) {
            reward += Rewards.LOSE.getValue();
            Rewards.logReward(Rewards.LOSE, "LOOSE");
        } else if (isDraw(newState)) {
            reward += Rewards.DRAW.getValue();
            Rewards.logReward(Rewards.DRAW, "Draw");
        }

        if (removedOpponentRing(previousState, newState, currentColor)) {
            reward += Rewards.OPPONENT_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.OPPONENT_RING_REMOVAL, "Opponent ring removed");
        }

        if (removedYourRing(previousState, newState, currentColor)) {
            reward += Rewards.YOUR_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.YOUR_RING_REMOVAL, "Your ring removed");
        }

        int[] flippedChips = countFlippedChips(previousState, newState, currentColor);
        int ownFlips = flippedChips[0];
        int opponentFlips = flippedChips[1];

        if (ownFlips > 0) {
            double flipReward = ownFlips * Rewards.FLIP_MARKERS.getValue();
            reward += flipReward;
            Rewards.logReward(Rewards.FLIP_MARKERS, "Flipped " + ownFlips + " chips to your color");
        }

        if (opponentFlips > 0) {
            double flipPenalty = opponentFlips * Rewards.FLIP_MARKERS_OPPONENT.getValue();
            reward += flipPenalty;
            Rewards.logReward(Rewards.FLIP_MARKERS_OPPONENT, "Flipped " + opponentFlips + " chips to opponent's color");
        }

        if (createdNewLine(previousState, newState, 3, currentColor)) {
            reward += Rewards.THREE_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.THREE_IN_A_ROW, "3 chips in a row");
        }

        if (createdNewLine(previousState, newState, 2, currentColor)) {
            reward += Rewards.TWO_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.TWO_IN_A_ROW, "2 chips in a row");
        }

        if (createdNewLine(previousState, newState, 5, currentColor)) {
            reward += Rewards.FIVE_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FIVE_IN_A_ROW, "5 chips in a row");
        }

        if (createdNewLine(previousState, newState, 4, currentColor)) {
            reward += Rewards.FOUR_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FOUR_IN_A_ROW, "4 chips in a row");
        }

        if (createdNewLine(previousState, newState, 5, currentColor)) {
            reward += Rewards.FIVE_CHIPS_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FIVE_CHIPS_IN_A_ROW, "5 chips in a row");
        }

        if (selfBlock(previousState, newState, currentColor)) {
            reward += Rewards.SELF_BLOCK.getValue();
            Rewards.logReward(Rewards.SELF_BLOCK, "Blocked own move");
        }

        if (newState.equals(previousState)) {
            reward += Rewards.INVALID_MOVE.getValue();
            Rewards.logReward(Rewards.INVALID_MOVE, "No progress detected");
        }

        if (successfulMove(move, previousState, newState)) {
            reward += Rewards.SUCCESSFUL_MOVE.getValue();
            Rewards.logReward(Rewards.SUCCESSFUL_MOVE, "Successful move made");
        }

        if (opponentCreatedLine(previousState, newState, 2, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_TWO.getValue(); 
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION_TWO, "Created a line of 2 for opponent");
        }
        
        if (opponentCreatedLine(previousState, newState, 3, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_THREE.getValue();
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION_THREE, "Created a line of 3 for opponent");
        }
        
        if (opponentCreatedLine(previousState, newState, 4, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_FOUR.getValue(); 
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION_FOUR, "Created a line of 4 for opponent");
        }

        return Rewards.normalizeReward(reward);
    }

    /**
     * Determines if a line of chips of a specified length was created for your opponent.
     */
    public static boolean opponentCreatedLine(GameState previousState, GameState newState, int length, String currentColor) {
        String opponentColor = currentColor.equalsIgnoreCase("white") ? "black" : "white";
        return countLinesForPlayer(newState, length, opponentColor) > countLinesForPlayer(previousState, length, opponentColor);
    }
    
    /**
     * Calculates the total reward for a given move and state transition. With no logs.
     *
     * @param engine        Game engine to evaluate the game state.
     * @param previousState The game state before the move.
     * @param move          The move performed.
     * @param newState      The game state after the move.
     * @param currentColor  The color of the current player ("white" or "black").
     * @return The normalized reward value.
     */
    public static double calculateRewardWITHOUT(GameEngine engine, GameState previousState, Move move, GameState newState, String currentColor) {
        double reward = 0.0;

        if (isWin(engine, newState, currentColor)) {
            // System.out.println("WINNER WINNER CHICKEN DINNER");
            reward += Rewards.WIN.getValue();
        } else if (isLose(engine, newState, currentColor)) {
            reward += Rewards.LOSE.getValue();
        } else if (isDraw(newState)) {
            reward += Rewards.DRAW.getValue();
        }

        if (removedOpponentRing(previousState, newState, currentColor)) {
            reward += Rewards.OPPONENT_RING_REMOVAL.getValue();
        }

        if (removedYourRing(previousState, newState, currentColor)) {
            // System.out.println(move);
            // System.out.println("reward for the removed ring");
            reward += Rewards.YOUR_RING_REMOVAL.getValue();
        }

        int[] flippedChips = countFlippedChips(previousState, newState, currentColor);
        int ownFlips = flippedChips[0];
        int opponentFlips = flippedChips[1];

        if (ownFlips > 0) {
            double flipReward = ownFlips * Rewards.FLIP_MARKERS.getValue();
            reward += flipReward;
        }

        if (opponentFlips > 0) {
            double flipPenalty = opponentFlips * Rewards.FLIP_MARKERS_OPPONENT.getValue();
            reward += flipPenalty;
        }

        if (createdNewLine(previousState, newState, 3, currentColor)) {
            reward += Rewards.THREE_IN_A_ROW.getValue();
        }

        if (createdNewLine(previousState, newState, 4, currentColor)) {
            reward += Rewards.FOUR_IN_A_ROW.getValue();
        }

        
        if (opponentCreatedLine(previousState, newState, 2, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_TWO.getValue(); 
        }
        
        if (opponentCreatedLine(previousState, newState, 3, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_THREE.getValue();
        }
        
        if (opponentCreatedLine(previousState, newState, 4, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_FOUR.getValue(); 
        }

        if (createdNewLine(previousState, newState, 5, currentColor)) {
            reward += Rewards.FIVE_CHIPS_IN_A_ROW.getValue();
        }

        if (createdNewLine(previousState, newState, 2, currentColor)) {
            reward += Rewards.TWO_IN_A_ROW.getValue();
        }

        if (selfBlock(previousState, newState, currentColor)) {
            reward += Rewards.SELF_BLOCK.getValue();
        }
        if (createdNewLine(previousState, newState, 5, currentColor)) {
            reward += Rewards.FIVE_IN_A_ROW.getValue();
        }

        if (newState.equals(previousState)) {
            reward += Rewards.INVALID_MOVE.getValue();
        }

        if (successfulMove(move, previousState, newState)) {
            reward += Rewards.SUCCESSFUL_MOVE.getValue();
        }

        return Rewards.normalizeReward(reward);
    }
   
    /**
     * Determines if a line of chips of a specified length was created.
     */
    public static boolean createdNewLine(GameState previousState, GameState newState, int length, String playerColor) {
        Set<String> previousLines = getAllLines(previousState, length, playerColor);
        Set<String> newLines = getAllLines(newState, length, playerColor);
        newLines.removeAll(previousLines); 
        return !newLines.isEmpty();
    }

    /**
     * Returns a set of all lines of a specific length for a player.
     */
    private static Set<String> getAllLines(GameState state, int length, String playerColor) {
        Set<String> lines = new HashSet<>();
        Vertex[][] board = state.getGameBoard().getBoard();
        playerColor = playerColor.toLowerCase();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null && board[i][j].hasCoin() && board[i][j].getCoin().getColour().equalsIgnoreCase(playerColor)) {
                    int start = board[i][j].getVertextNumber();

                    for (Direction dir : Direction.getPrimaryDirections()) {
                        int lineLength = countChipsInOneDirection(state, start, playerColor, dir.getDeltaX(), dir.getDeltaY());
                        if (lineLength + 1 >= length) {
                            String lineIdentifier = generateLineIdentifier(start, dir, lineLength + 1);
                            lines.add(lineIdentifier);
                        }
                    }
                }
            }
        }
        return lines;
    }



    /**
     * Checks if the current player wins the game.
     */
    public static boolean isWin(GameEngine gameEngine, GameState state, String currentColor) {
        if (currentColor.toLowerCase().equals("white")) {
            return state.ringsWhite <= 2 && state.chipsRemaining >= 0; 
        } else if (currentColor.toLowerCase().equals("black")) {
            return state.ringsBlack <= 2 && state.chipsRemaining >= 0; 
        }else if(state.winner!=null && currentColor.toLowerCase().equals(state.winner) && state.chipsRemaining >= 0){
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the current player loses the game.
     */
    public static boolean isLose(GameEngine gameEngine, GameState state, String currentColor) {
        if (currentColor.toLowerCase().equals("white")) {
            return state.ringsBlack <= 2 && state.chipsRemaining >= 0; 
        } else if (currentColor.toLowerCase().equals("black")) {
            return state.ringsWhite <= 2 && state.chipsRemaining >= 0; 
        } else if(state.winner!=null && !currentColor.toLowerCase().equals(state.winner) && state.chipsRemaining >= 0){
            return true;
        }
        return false;
    }
    

    /**
     * Checks for the draw.
     */
    public static boolean isDraw(GameState state) {
        return state.chipsRemaining <= 0;
    }

    /**
     * Checks if the current player has had one of their rings removed.
     *
     * @param previousState The game state before the move.
     * @param newState      The game state after the move.
     * @param currentColor  The current player's color ("white" or "black").
     * @return {@code true} if the player's ring count decreased; {@code false} otherwise.
     */
    public static boolean removedYourRing(GameState previousState, GameState newState, String currentColor) {
        return newState.getRingCountForColor(currentColor.toLowerCase()) < previousState.getRingCountForColor(currentColor.toLowerCase());
    }


    /**
     * Determines if the player blocked their own potential line of chips.
     *
     * @param previousState The game state before the move.
     * @param newState      The game state after the move.
     * @param currentColor  The current player's color ("white" or "black").
     * @return {@code true} if the number of 3-chip lines decreased; {@code false} otherwise.
     */
    public static boolean selfBlock(GameState previousState, GameState newState, String currentColor) {
        String currentPlayerColor = previousState.currentPlayerColor().toLowerCase();
        return countLinesForPlayer(previousState, 3, currentPlayerColor) >
                countLinesForPlayer(newState, 3, currentPlayerColor);
    }

    /**
     * Checks if the current player successfully removed one of the opponent's rings.
     *
     * @param previousState The game state before the move.
     * @param newState      The game state after the move.
     * @param currentColor  The current player's color ("white" or "black").
     * @return {@code true} if the opponent's ring count decreased; {@code false} otherwise.
     */
    public static boolean removedOpponentRing(GameState previousState, GameState newState, String currentColor) {
        String opponentColor = currentColor.toLowerCase().equals("white") ? "black" : "white";
        return newState.getRingCountForColor(opponentColor) < previousState.getRingCountForColor(opponentColor);
    }
    
    /**
     * Checks if the move resulted in a valid state change.
     *
     * @param move          The move performed.
     * @param previousState The game state before the move.
     * @param newState      The game state after the move.
     * @return {@code true} if the new state is different from the previous state; {@code false} otherwise.
     */
    public static boolean successfulMove(Move move, GameState previousState, GameState newState) {
        return !newState.equals(previousState); // Any valid state change is a success
    }

    /**
     * Counts the number of lines of a specific length for the player.
     */
    public static int countLinesForPlayer(GameState state, int length, String playerColor) {
        int count = 0; 
        Vertex[][] board = state.getGameBoard().getBoard();
        Set<String> countedLines = new HashSet<>();     
        playerColor = playerColor.toLowerCase();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null && board[i][j].hasCoin()) {
                    if (board[i][j].getCoin().getColour().equalsIgnoreCase(playerColor)) {
                        int start = board[i][j].getVertextNumber(); 
    
                        for (Direction dir : Direction.getPrimaryDirections()) { 
                            int lineLength = countChipsInOneDirection(state, start, playerColor, dir.getDeltaX(), dir.getDeltaY());
    
                            if (lineLength + 1 == length) {
                                String lineIdentifier = generateLineIdentifier(start, dir, length); 
                                if (!countedLines.contains(lineIdentifier)) {
                                    countedLines.add(lineIdentifier); 
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
    
        return count;
    }
    
    /**
     * Counts the number of new chips.
     */
    private static int countChipsInOneDirection(GameState state, int start, String chipColor, int dx, int dy) {
        int count = 0;
        int x = state.getGameBoard().getVertex(start).getXposition();
        int y = state.getGameBoard().getVertex(start).getYposition();

        while (true) {
            x += dx;
            y += dy;
            int nextVertex = state.getGameBoard().getVertexNumberFromPosition(x, y);

            if (nextVertex == -1) { 
                break;
            }

            Vertex v = state.getGameBoard().getVertex(nextVertex);
            if (v == null || !v.hasCoin() || !v.getCoin().getColour().equalsIgnoreCase(chipColor)) {
                break;
            }

            count++;
        }
        return count;
    }

    /**
     * Counts the number of chips flipped during a move.
     */
    public static int[] countFlippedChips(GameState previousState, GameState newState, String currentColor) {
        int ownFlips = 0;
        int opponentFlips = 0;
        Vertex[][] prevBoard = previousState.getGameBoard().getBoard();
        Vertex[][] newBoard = newState.getGameBoard().getBoard();
        String opponentColor = currentColor.equalsIgnoreCase("white") ? "black" : "white";

        for (int i = 0; i < prevBoard.length; i++) {
            for (int j = 0; j < prevBoard[i].length; j++) {
                Vertex prevVertex = prevBoard[i][j];
                Vertex newVertex = newBoard[i][j];

                if (prevVertex != null && newVertex != null && prevVertex.hasCoin() && newVertex.hasCoin()) {
                    if (prevVertex.getCoin().getColour().equalsIgnoreCase(opponentColor) &&
                        newVertex.getCoin().getColour().equalsIgnoreCase(currentColor)) {
                        ownFlips++;
                    } else if (prevVertex.getCoin().getColour().equalsIgnoreCase(currentColor) &&
                               newVertex.getCoin().getColour().equalsIgnoreCase(opponentColor)) {
                        opponentFlips++;
                    }
                }
            }
        }

        return new int[]{ownFlips, opponentFlips};
    }

    /**
     * Generates a unique identifier for a line of chips.
     */
    private static String generateLineIdentifier(int startVertex, Direction dir, int length) {
        return startVertex + "-" + dir.name() + "-" + length;
    }


}
