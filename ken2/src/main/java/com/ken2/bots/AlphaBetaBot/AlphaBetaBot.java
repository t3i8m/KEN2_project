package com.ken2.bots.AlphaBetaBot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ken2.Game_Components.Board.*;
import com.ken2.bots.BotAbstract;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.ui.GameAlerts;


public class AlphaBetaBot extends BotAbstract{
    private GameState StateRightNow;
    // public int depth = 3;

    public AlphaBetaBot(String color){
        super(color);
    }

    public Move makeMove(GameState state) {
        this.StateRightNow = state.clone();
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        GameState stateClone = state.clone(); // clone pattern (prototype)
        GameEngine ge = new GameEngine();
        Game_Board board = state.getGameBoard();
        Random random = new Random();

        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
            System.out.println("No free positions available on the board.");
            return null;
        }

        if (state.ringsPlaced < 10) {
            Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size()));

            int vertexNumber = targetVertex.getVertextNumber();
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(super.getColor());
            board.updateBoard(vertexNumber, ring);
            state.ringsPlaced++;
            this.StateRightNow = state;
            return new Move(targetPosition[0], targetPosition[1], null);
        }

        AlphaBetaResult result = alphaBeta(this.StateRightNow, alpha, beta, 3, ge);

        if (result != null && result.getMove() != null) {
            System.out.println("Bro executed move: " + result.getMove());
            return result.getMove();
        } else {
            System.out.println("wtf, no valid move found.");
            return null;
        }
    }

    public AlphaBetaResult alphaBeta(GameState state, double alpha, double beta, int depth, GameEngine ge) {
        // System.out.println("AAAAAAAA state after move:");
        // System.out.println(this.StateRightNow.getGameBoard().strMaker());
        state = this.StateRightNow;

        // System.out.println("Entering alphaBeta with depth: " + depth + ", player: " + state.currentPlayerColor());
        // System.out.println("depth: " + depth);

        if (depth == 0) {
            // System.out.println("Reached base case at depth 0");
            return new AlphaBetaResult(evaluate(this.StateRightNow, ge, this.StateRightNow.currentPlayerColor()), null);
        }

        Move bestMove = null;
        double value;
        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions);

        if (vertexMove.isEmpty()) {
            System.out.println("No moves available, evaluating...");
            return new AlphaBetaResult(evaluate(state, ge, state.currentPlayerColor()), null);
        }

        if (state.currentPlayerColor().toLowerCase().equals(this.getColor().toLowerCase())) {
            value = Double.NEGATIVE_INFINITY;

            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                Vertex key = entry.getKey();
                ArrayList<Move> possibleMovesFromThisVertex = entry.getValue();

                for (Move m : possibleMovesFromThisVertex) {
                    GameState newState = moveState(state, m);
                    if (newState.equals(state)) {
                        continue;
                    }
                    newState.switchPlayer();

                    AlphaBetaResult result = alphaBeta(newState, alpha, beta, depth - 1, ge);

                    if (result.getValue() > value) {
                        value = result.getValue();
                        bestMove = m;
                    }
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) break;
                }
            }
            return new AlphaBetaResult(value, bestMove);

        } else {
            value = Double.POSITIVE_INFINITY;

            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                Vertex key = entry.getKey();
                ArrayList<Move> possibleMovesFromThisVertex = entry.getValue();
                if (possibleMovesFromThisVertex.isEmpty()) {
                    continue;
                }
                for (Move m : possibleMovesFromThisVertex) {
                    GameState newState = moveState(state, m);
                    if (newState.equals(state)) {
                        continue;
                    }
                    newState.switchPlayer();

                    AlphaBetaResult result = alphaBeta(newState, alpha, beta, depth - 1, ge);

                    if (result.getValue() < value) {
                        value = result.getValue();
                        bestMove = m;
                    }
                    beta = Math.min(beta, value);
                    if (alpha >= beta) break;
                }
            }
            return new AlphaBetaResult(value, bestMove);
        }
    }

    private double evaluate(GameState state, GameEngine ge, String color) {
        double valuation = 0;
        String opponentColor = color.equals("white") ? "black" : "white";

        double inOurfavour = 0.50;
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
        GameState newState = this.StateRightNow.clone();

        GameEngine tempEngine = new GameEngine();
        tempEngine.currentState = newState;
        tempEngine.gameBoard = newState.gameBoard;

        // System.out.println("Current board: " + newState.gameBoard.strMaker());
        // System.out.println(move.getXposition());
        // System.out.println(move.getYposition());
        // System.out.println(move.getStartingVertex().getVertextNumber());

        int toVertex = tempEngine.gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
        if (toVertex == -1) {
            // System.out.println("Invalid target vertex.");
            return tempEngine.currentState;
        }

        Vertex sourceVertex = move.getStartingVertex();
        Vertex targetVertex = tempEngine.currentState.gameBoard.getVertex(toVertex);
        // System.out.println(toVertex);
        // System.out.println(targetVertex.getVertextNumber());
        if (targetVertex == null || targetVertex.hasRing() || targetVertex.hasCoin()) {
            // System.out.println("Invalid move: target vertex is not empty.");
            return tempEngine.currentState;
        }

        if (sourceVertex.getVertextNumber() == targetVertex.getVertextNumber()) {
            // System.out.println("SAME");
            return tempEngine.currentState;
        }

        if (!tempEngine.placeChipAB(sourceVertex.getVertextNumber())) {
            return tempEngine.currentState;
        }
        // tempEngine.placeChipAB(sourceVertex.getVertextNumber());
        Ring ringToMove = (Ring) sourceVertex.getRing();
        // System.out.println(ringToMove);
        if (ringToMove != null) {
            sourceVertex.setRing(null);

            tempEngine.gameBoard.the_Board[sourceVertex.getXposition()][sourceVertex.getYposition()].setRing(null);

            ArrayList<Coin> flippedCoins = move.getFlippedCoins();
            ArrayList<Vertex> vertexesOfFlippedCoins = new ArrayList<>();
            if (!flippedCoins.isEmpty()) {
                tempEngine.gameSimulation.flipCoins(flippedCoins, tempEngine.currentState.gameBoard);

                for (Coin c : flippedCoins) {
                    Vertex currVertex = tempEngine.currentState.gameBoard.getVertexByCoin(c);
                    vertexesOfFlippedCoins.add(currVertex);
                }
            }

            targetVertex.setRing(ringToMove);
            tempEngine.currentState.chipRingVertex = -1;
            tempEngine.currentState.chipsRemaining -= 1;
            tempEngine.currentState.chipPlaced = false;
            tempEngine.currentState.selectedRingVertex = -1;
            tempEngine.currentState.updateChipsRingCountForEach();
            vertexesOfFlippedCoins.add(sourceVertex);
            tempEngine.currentState.setVertexesOfFlippedCoins(vertexesOfFlippedCoins);
            newState = tempEngine.currentState;
            newState.gameBoard = tempEngine.currentState.gameBoard;
            newState.gameBoard.updateBoard(toVertex, new Ring(state.currentPlayerColor()));
            // System.out.println("New state after move: " + newState.gameBoard.strMaker());
            this.StateRightNow = tempEngine.currentState;
            return tempEngine.currentState;

        } else {
            // System.out.println("New state after move DID NOT MAKE CHANGES: " + newState.gameBoard.strMaker());
            this.StateRightNow = tempEngine.currentState;
            return tempEngine.currentState;
        }
    }

    public void setState(GameState state) {
        this.StateRightNow = state;
    }

    public GameState getState() {
        return this.StateRightNow;
    }
}
