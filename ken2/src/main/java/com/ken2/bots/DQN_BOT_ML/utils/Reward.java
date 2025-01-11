package com.ken2.bots.DQN_BOT_ML.utils;

import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Direction;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

public class Reward {

    public static double calculateReward(GameEngine engine, GameState previousState, Move move, GameState newState) {
        double reward = 0.0;
        if (isWin(engine,newState)) {
            reward += Rewards.WIN.getValue();
            Rewards.logReward(Rewards.WIN, "VICTORY");
        } else if (isLose(newState)) {
            reward += Rewards.LOSE.getValue();
            Rewards.logReward(Rewards.LOSE, "LOSE ЕЩКЕРЕ Я РАНЯЮ ЗАПАД УУУУУ");
        }

        if (isDraw(newState)) {
            reward += Rewards.DRAW.getValue();
            Rewards.logReward(Rewards.DRAW, "Draw");
        }

        if (removedOpponentRing(previousState, newState)) {
            reward += Rewards.OPPONENT_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.OPPONENT_RING_REMOVAL, "Opponent ring removed");
        }
        if (removedYourRing(previousState, newState)) {
            reward += Rewards.YOUR_RING_REMOVAL.getValue();
            Rewards.logReward(Rewards.YOUR_RING_REMOVAL, "Your ring removed");
        }

        if (createdLine(newState, 5)) {
            reward += Rewards.FIVE_CHIPS_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FIVE_CHIPS_IN_A_ROW, "5 chips in a row");
        } else if (createdLine(newState, 4)) {
            reward += Rewards.FOUR_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.FOUR_IN_A_ROW, "4 chips in a row");
        } else if (createdLine(newState, 3)) {
            reward += Rewards.THREE_IN_A_ROW.getValue();
            Rewards.logReward(Rewards.THREE_IN_A_ROW, "3 chips in a row");
        }
        if (doubleRowCreated(newState)) {
            reward += Rewards.DOUBLE_ROW_CREATION.getValue();
            Rewards.logReward(Rewards.DOUBLE_ROW_CREATION, "Double row created");
        }

//        if (flippedMarkers(previousState, newState)) {
//            reward += Rewards.FLIP_MARKERS.getValue();
//            Rewards.logReward(Rewards.FLIP_MARKERS, "Markers flipped");
//        }
        if (selfBlock(previousState, newState)) {
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

        if (opponentCreatedLine(previousState, newState)) {
            reward += Rewards.OPPONENT_ROW_CREATION.getValue();
            Rewards.logReward(Rewards.OPPONENT_ROW_CREATION, "Opponent row created!");
        }

        return Rewards.normalizeReward(reward);
    }

    public static boolean isWin(GameEngine gameEngine, GameState state) {
        return gameEngine.win(state.getVertexesOfFlippedCoins());
    }

    public static boolean isLose(GameState state) {
        if(state.ringsPlaced == 10){
            String opponentColor = state.currentPlayerColor().equals("white") ? "black" : "white";
            int initialRingCount = 5;
            int ringsRemovedByOpponent = initialRingCount - state.getRingCountForColor(opponentColor);
            return ringsRemovedByOpponent >= 3;
        }
        else return false;
    }


    public static boolean isDraw(GameState state) {
        return state.chipsRemaining <= 0;
    }
    public static boolean removedYourRing(GameState previousState, GameState newState) {
        return newState.getRingCountForColor(newState.currentPlayerColor()) < previousState.getRingCountForColor(previousState.currentPlayerColor());
    }
    public static boolean doubleRowCreated(GameState state) {
        String currentPlayerColor = state.currentPlayerColor();
        return countLinesForPlayer(state, 5, currentPlayerColor) >= 2; // at least 2 rows of length 5
    }
//    public static boolean flippedMarkers(GameState previousState, GameState newState) {
//    }

    public static boolean selfBlock(GameState previousState, GameState newState) {
        String currentPlayerColor = previousState.currentPlayerColor();
        return countLinesForPlayer(previousState, 3, currentPlayerColor) >
                countLinesForPlayer(newState, 3, currentPlayerColor);
    }


    public static boolean removedOpponentRing(GameState previousState, GameState newState) {
        String opponentColor = previousState.currentPlayerColor().equals("white") ? "black" : "white";
        return newState.getRingCountForColor(opponentColor) < previousState.getRingCountForColor(opponentColor);
    }

    public static boolean createdLine(GameState state, int length) {
        String currentPlayerColor = state.currentPlayerColor();
        return countLinesForPlayer(state, length, currentPlayerColor) > 0;
    }

    public static boolean opponentCreatedLine (GameState previousState, GameState newState) {
        String opponentColor = newState.currentPlayerColor();
        if(opponentColor.equalsIgnoreCase("white"))
            opponentColor.equalsIgnoreCase("black");
        else opponentColor.equalsIgnoreCase("white");
        return countLinesForPlayer(newState, 3, opponentColor) > countLinesForPlayer(previousState, 3, opponentColor);
    }

    public static boolean successfulMove(Move move, GameState previousState, GameState newState) {
        return !newState.equals(previousState); // Any valid state change is a success
    }


    public static int countLinesForPlayer(GameState state, int length, String playerColor) {
        int count = 0;
        Vertex[][] board = state.getGameBoard().getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null && board[i][j].hasCoin()) {
                    if (board[i][j].getCoin().getColour().equalsIgnoreCase(playerColor)) {
                        int start = board[i][j].getVertextNumber();
                        for (Direction dir : Direction.values()) {
                            int lineLength = countChipsInOneDirection(state, start, playerColor, dir.getDeltaX(), dir.getDeltaY());
                            if (lineLength + 1 == length) {
                                count++;
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    private static int countChipsInOneDirection(GameState state, int start, String chipColor, int dx, int dy) {
        int k = 0;
        int x = state.getGameBoard().getVertex(start).getXposition();
        int y = state.getGameBoard().getVertex(start).getYposition();

        // Traverse in the given direction
        while (true) {
            x += dx;
            y += dy;
            int next = state.getGameBoard().getVertexNumberFromPosition(x, y);
            if (next == -1) {
                return k;
            }

            Vertex v = state.getGameBoard().getVertex(next);
            if (v == null || !v.hasCoin() || !v.getCoin().getColour().equalsIgnoreCase(chipColor)) {
                return k;
            }

            k++;
        }
    }
}