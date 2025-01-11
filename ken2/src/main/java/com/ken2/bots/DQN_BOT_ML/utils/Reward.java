package com.ken2.bots.DQN_BOT_ML.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Direction;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class Reward {

    public static double calculateReward(GameEngine engine, GameState previousState, Move move, GameState newState, String currentColor) {
        double reward = 0.0;

        if (isWin(engine,newState, currentColor)) {
            reward += Rewards.WIN.getValue();
            Rewards.logReward(Rewards.WIN, "VICTORY");
        } else if (isLose(engine,newState, currentColor)) {
            reward += Rewards.LOSE.getValue();
            Rewards.logReward(Rewards.LOSE, "LOOSE");
        } else if (isDraw(newState) ) {
            reward += Rewards.DRAW.getValue();
            Rewards.logReward(Rewards.DRAW, "Draw");
        }

        if (removedOpponentRing(previousState, newState, currentColor)) {
            reward += Rewards.OPPONENT_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.OPPONENT_RING_REMOVAL, "Opponent ring removed");
            reward += Rewards.OPPONENT_ROW_CREATION_FIVE.getValue();
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION_FIVE, "Opponent row created 5 chips!");
        }
        if (removedYourRing(previousState, newState, currentColor)) {
            reward += Rewards.YOUR_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.YOUR_RING_REMOVAL, "Your ring removed");
            reward += Rewards.FIVE_CHIPS_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FIVE_CHIPS_IN_A_ROW, "5 chips in a row");
        }

        
        if (createdLine(newState,previousState, 3, currentColor)) {
            if (createdLine(newState,previousState, 4, currentColor)) {
                reward += Rewards.FOUR_IN_A_ROW.getValue();
                Rewards.logReward(Rewards.FOUR_IN_A_ROW, "4 chips in a row");}
            else{
                reward += Rewards.THREE_IN_A_ROW.getValue();
                Rewards.logReward(Rewards.THREE_IN_A_ROW, "3 chips in a row");
            }
            
        }
        if (doubleRowCreated(newState, currentColor)) {
            reward += Rewards.DOUBLE_ROW_CREATION.getValue();
            Rewards.logReward(Rewards.DOUBLE_ROW_CREATION, "Double row created");
        }

//        if (flippedMarkers(previousState, newState)) {
//            reward += Rewards.FLIP_MARKERS.getValue();
//            Rewards.logReward(Rewards.FLIP_MARKERS, "Markers flipped");
//        }
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

        if (opponentCreatedLine(previousState, newState, currentColor)) {
            reward += Rewards.OPPONENT_ROW_CREATION_THREE.getValue();
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION_THREE, "Opponent row created 3 chips!");
        }

        return Rewards.normalizeReward(reward);
    }


    public static boolean isWin(GameEngine gameEngine, GameState state, String currentColor) {
        if (currentColor.toLowerCase().equals("white")) {
            return state.ringsWhite <= 2 && state.chipsRemaining >= 0; 
        } else if (currentColor.toLowerCase().equals("black")) {
            return state.ringsBlack <= 2 && state.chipsRemaining >= 0; 
        }
        return false;
    }
    

    public static boolean isLose(GameEngine gameEngine, GameState state, String currentColor) {
        if (currentColor.toLowerCase().equals("white")) {
            return state.ringsBlack <= 2 && state.chipsRemaining >= 0; 
        } else if (currentColor.toLowerCase().equals("black")) {
            return state.ringsWhite <= 2 && state.chipsRemaining >= 0; 
        }
        return false;
    }
    


    public static boolean isDraw(GameState state) {
        return state.chipsRemaining <= 0;
    }
    public static boolean removedYourRing(GameState previousState, GameState newState, String currentColor) {

        return newState.getRingCountForColor(currentColor.toLowerCase()) < previousState.getRingCountForColor(currentColor.toLowerCase());
    }
    public static boolean doubleRowCreated(GameState state, String currentColor) {
        String currentPlayerColor = currentColor;
        return countLinesForPlayer(state, 5, currentPlayerColor) >= 2; // at least 2 rows of length 5
    }
//    public static boolean flippedMarkers(GameState previousState, GameState newState) {
//    }

    public static boolean selfBlock(GameState previousState, GameState newState, String currentColor) {
        String currentPlayerColor = previousState.currentPlayerColor();
        return countLinesForPlayer(previousState, 3, currentPlayerColor) >
                countLinesForPlayer(newState, 3, currentPlayerColor);
    }


    public static boolean removedOpponentRing(GameState previousState, GameState newState, String currentColor) {
        String opponentColor = currentColor.equals("white") ? "black" : "white";
        return newState.getRingCountForColor(opponentColor) < previousState.getRingCountForColor(opponentColor);
    }

    public static boolean createdLine(GameState state,GameState prevState, int length, String currentColor) {
        String currentPlayerColor = currentColor.toLowerCase();
        return countLinesForPlayer(state, length, currentPlayerColor) >0;
    }

    public static boolean opponentCreatedLine(GameState previousState, GameState newState, String currentColor) {
        String opponentColor = currentColor.equals("white") ? "black" : "white"; 
        return countLinesForPlayer(newState, 3, opponentColor) > countLinesForPlayer(previousState, 3, opponentColor);
    }
    

    public static boolean successfulMove(Move move, GameState previousState, GameState newState) {
        return !newState.equals(previousState); // Any valid state change is a success
    }


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
                                    // System.out.println("Line of length " + length + " found starting at vertex " + start + " in direction " + dir.name()+" "+playerColor);
                                }
                            }
                        }
                    }
                }
            }
        }
    
        return count;
    }
    

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

    private static String generateLineIdentifier(int startVertex, Direction dir, int length) {
        return startVertex + "-" + dir.name() + "-" + length;
    }


}
