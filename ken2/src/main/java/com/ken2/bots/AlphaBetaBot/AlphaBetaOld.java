package com.ken2.bots.AlphaBetaBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ken2.Game_Components.Board.*;
import com.ken2.bots.BotAbstract;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

import javafx.scene.control.skin.TextInputControlSkin.Direction;

public class AlphaBetaOld extends BotAbstract {
    private GameState StateRightNow;

    public AlphaBetaOld(String color) {
        super(color);
    }

    @Override
    public String getName() {
        return "AlphaBetaOld";
    }

    public Move makeMove(GameState state) {
        this.StateRightNow = state.clone();
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        GameEngine ge = new GameEngine();
        Game_Board board = state.getGameBoard();
        Random random = new Random();

        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
            // System.out.println("No free positions available on the board.");
            return null;
        }

        if (state.ringsPlaced < 10) {
            Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size()));

            int vertexNumber = targetVertex.getVertextNumber();
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(super.getColor());
            board.updateBoard(vertexNumber, ring);
            state.ringsPlaced++;
            return new Move(targetPosition[0], targetPosition[1], null);
        }

        AlphaBetaResult result = alphaBeta(this.StateRightNow, alpha, beta, 1, ge);

        if (result != null && result.getMove() != null) {
            // System.out.println("Executed move: " + result.getMove());
            return result.getMove();
        } else {
            // System.out.println("No valid move found.");
            return null;
        }
    }

    public AlphaBetaResult alphaBeta(GameState state, double alpha, double beta, int depth, GameEngine ge) {
        if (depth == 0) {
            return new AlphaBetaResult(evaluate(state, ge, state.currentPlayerColor()), null);
        }
        GameEngine ge2 = new GameEngine();
        Move bestMove = null;
        double value;
        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions, state.gameBoard);
        GameSimulation gs = new GameSimulation();
        if (vertexMove.isEmpty()) {
            return new AlphaBetaResult(evaluate(state, ge, state.currentPlayerColor()), null);
        }

        if (state.currentPlayerColor().equalsIgnoreCase(this.getColor())) {
            value = Double.NEGATIVE_INFINITY;

            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                for (Move m : entry.getValue()) {
                    if (entry == null) {
                        continue;
                    }

                    int vertexFrom = m.getStartingVertex().getVertextNumber();
                    int vertexTo = state.gameBoard.getVertexNumberFromPosition(m.getXposition(), m.getYposition());

                    if (!isValidMove(state, m)) {
                        continue;
                    }
                    if (vertexFrom < 0 || vertexTo < 0) {
                        continue;
                    }

                    Vertex targetVertex = state.getGameBoard().getVertex(
                            state.getGameBoard().getVertexNumberFromPosition(m.getXposition(), m.getYposition())
                    );
                    if (targetVertex == null || targetVertex.hasCoin() || targetVertex.hasRing()) continue;

                    // m = gs.simulateMove(state.gameBoard, m.getStartingVertex(), state.gameBoard.getVertex(vertexTo));

                    GameState newState = moveState(state, m);
                    newState.switchPlayer();

                    AlphaBetaResult result = alphaBeta(newState, alpha, beta, depth - 1, ge);

                    if (result.getValue() > value) {
                        value = result.getValue();
                        bestMove = m;
                    }
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) break;
                }
                if (alpha >= beta) break;
            }
            return new AlphaBetaResult(value, bestMove);

        } else {
            value = Double.POSITIVE_INFINITY;

            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                for (Move m : entry.getValue()) {
                    if (entry == null) {
                        continue;
                    }
                    int vertexFrom = m.getStartingVertex().getVertextNumber();
                    int vertexTo = state.gameBoard.getVertexNumberFromPosition(m.getXposition(), m.getYposition());

                    if (!isValidMove(state, m)) {
                        continue;
                    }
                    if (vertexFrom < 0 || vertexTo < 0) {
                        continue;
                    }
                    Vertex targetVertex = state.getGameBoard().getVertex(
                            state.getGameBoard().getVertexNumberFromPosition(m.getXposition(), m.getYposition())
                    );
                    if (targetVertex == null || targetVertex.hasCoin() || targetVertex.hasRing()) continue;
                    // m = gs.simulateMove(state.gameBoard, m.getStartingVertex(), state.gameBoard.getVertex(vertexTo));

                    GameState newState = moveState(state, m);
                    newState.switchPlayer();

                    AlphaBetaResult result = alphaBeta(newState, alpha, beta, depth - 1, ge);

                    if (result.getValue() < value) {
                        value = result.getValue();
                        bestMove = m;
                    }
                    beta = Math.min(beta, value);
                    if (alpha >= beta) break;
                }
                if (alpha >= beta) break;
            }
            return new AlphaBetaResult(value, bestMove);
        }
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

    private boolean isValidMove(GameState state, Move move) {
        Vertex[][] board = state.getGameBoard().getBoard();
        Vertex startVertex = move.getStartingVertex();

        if (startVertex == null || !startVertex.hasRing()) {
            return false;
        }

        PlayObj ring = (Ring) startVertex.getRing();

        if (!ring.getColour().equals(state.currentPlayerColor())) {
            return false;
        }

        int startX = startVertex.getXposition();
        int startY = startVertex.getYposition();
        int targetX = move.getXposition();
        int targetY = move.getYposition();

        com.ken2.engine.Direction direction = move.getDirection();

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
}