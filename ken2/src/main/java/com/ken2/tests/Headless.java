package com.ken2.tests;

import com.ken2.bots.Bot;
import com.ken2.utils.BotFactory;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Coin;

import java.util.ArrayList;
import java.util.List;

public class Headless {

    public static void main(String[] args) {

        int games = 200;

        BotFactory factory = new BotFactory();
        Bot whiteBot = factory.getBot("alphabeta bot", "White");
        Bot blackBot = factory.getBot("alphabeta bot", "Black");

        int whiteWins = 0;
        int blackWins = 0;
        int draws     = 0;

        for (int i = 0; i < games; i++) {
            String result;
            try {
                result = runSingleGame(whiteBot, blackBot);
            } catch (Exception ex) {
                result = "u";
            }

            switch (result) {
                case "white": whiteWins++; break;
                case "black": blackWins++; break;
                case "draw":  draws++;     break;

            }
        }

        System.out.println("=========================================");
        System.out.println("Games played:  " + games);
        System.out.println("White wins:    " + whiteWins);
        System.out.println("Black wins:    " + blackWins);
        System.out.println("Draws:         " + draws);
        System.out.println("=========================================");
    }

    private static String runSingleGame(Bot whiteBot, Bot blackBot) {

        GameEngine gameEngine = new GameEngine();
        gameEngine.resetGame();

        gameEngine.currentState.chipsRemaining = 51;

        int whiteScore = 0;
        int blackScore = 0;

        while (true) {

            GameState state = gameEngine.currentState;
            Game_Board board = state.getGameBoard();

            if (state.chipsRemaining <= 0) {
                return "draw";
            }
            if (whiteScore == 3) {
                return "white";
            }
            if (blackScore == 3) {
                return "black";
            }

            Bot currentBot = state.isWhiteTurn ? whiteBot : blackBot;

            if (state.ringsPlaced < 10) {

                Move ringPlacement = currentBot.makeMove(state);
                int chosenVertex = board.getVertexNumberFromPosition(
                    ringPlacement.getXposition(),
                    ringPlacement.getYposition()
                );

                Vertex boardV = board.getVertex(chosenVertex);
                Ring ring = new Ring(currentBot.getColor());
                boardV.setPlayObject(ring);

                state.ringsPlaced++;
                state.resetTurn();
                switchTurn(state);

            } else {

                Move move = currentBot.makeMove(state);
                if (move == null) {
                    return "draw";
                }

                Vertex fromVertex = move.getStartingVertex();
                if (fromVertex == null) {
                    return "draw";
                }

                int fromIndex = fromVertex.getVertextNumber();
                int toIndex   = board.getVertexNumberFromPosition(
                                    move.getXposition(),
                                    move.getYposition()
                                );

                gameEngine.placeChip(fromIndex);

                Move currentMove = gameEngine.gameSimulation.simulateMove(
                    board,
                    board.getVertex(fromIndex),
                    board.getVertex(toIndex)
                );

                Ring ringToMove;
                Vertex sourceV = board.getVertex(fromIndex);

                if (sourceV.hasRing()) {
                    ringToMove = (Ring) sourceV.getRing();
                    sourceV.setRing(null);
                } else {
                    return state.isWhiteTurn ? "black" : "white";
                }

                List<Coin> flippedCoins = currentMove.getFlippedCoins();
                ArrayList<Vertex> verticesToFlip = new ArrayList<>();

                if (flippedCoins != null) {
                    for (Coin coin : flippedCoins) {
                        Vertex coinVert = board.getVertex(coin.getVertex());
                        if (coinVert != null && !verticesToFlip.contains(coinVert)) {
                            verticesToFlip.add(coinVert);
                        }
                    }
                }

                gameEngine.gameSimulation.flipCoinsByVertex(verticesToFlip, board);

                Vertex targetV = board.getVertex(toIndex);
                if (targetV == null || targetV.hasRing()) {
                    return state.isWhiteTurn ? "black" : "white";
                }

                targetV.setRing(ringToMove);
                state.chipsRemaining--;

                boolean isWinningRow = gameEngine.win(state.getVertexesOfFlippedCoins());
                if (isWinningRow) {

                    String winnerColor = gameEngine.getWinningColor();
                    List<Integer> winningChips = gameEngine.getWinningChips();

                    if (winningChips != null && !winningChips.isEmpty()) {
                        for (int chipVertex : winningChips) {
                            Vertex chipV = board.getVertex(chipVertex);
                            if (chipV != null && chipV.hasCoin()) {
                                chipV.setCoin(null);
                            }
                        }
                    }

                    int ringVertexToRemove = findRingVertexToRemove(board, winnerColor);
                    if (ringVertexToRemove != -1) {
                        Vertex ringV = board.getVertex(ringVertexToRemove);
                        if (ringV.hasRing()) {
                            ringV.setRing(null);
                        }
                    }

                    if (winnerColor.toLowerCase().equalsIgnoreCase("white")) {
                        whiteScore++;
                    } else {
                        blackScore++;
                    }

                    if (whiteScore == 3) {
                        return "white";
                    }
                    if (blackScore == 3) {
                        return "black";
                    }
                }

                state.resetTurn();
                switchTurn(state);
            }
        }
    }

    private static void switchTurn(GameState state) {
        state.isWhiteTurn = !state.isWhiteTurn;
    }

    private static int findRingVertexToRemove(Game_Board board, String color) {
        for (int i = 0; i < 85; i++) {
            Vertex v = board.getVertex(i);
            if (v != null && v.hasRing()) {
                Ring r = (Ring) v.getRing();
                if (r.getColour().equalsIgnoreCase(color)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
