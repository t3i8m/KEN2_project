package com.ken2_1.bots.MLABbot;

import com.ken2_1.Game_Components.Board.*;
import com.ken2_1.bots.AlphaBetaBot.AlphaBetaResult;
import com.ken2_1.bots.Bot;
import com.ken2_1.bots.BotAbstract;
import com.ken2_1.bots.DQN_BOT_ML.utils.Reward;
import com.ken2_1.engine.GameEngine;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;

import java.util.*;

public class MLAlphaBetaBot extends BotAbstract{


    private GameState StateRightNow;
    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();
    private boolean switched = false;

    private Learnings weights;


    public MLAlphaBetaBot(String color) {
        super(color);
        weights = new Learnings(color);
    }

    public Move makeMove(GameState state) {

        // System.out.println(weights.getRewardsAsString());

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
        StateRightNow.setCurrentPlayer(super.getColor().toLowerCase());
        AlphaBetaResult result = alphaBeta(this.StateRightNow,this.StateRightNow.clone(), alpha, beta, 1, ge, null);
        // StateRightNow.switchPlayerNEW();
        // state.switchPlayerNEW();
        if (result != null && result.getMove() != null) {
            // System.out.println("Executed move: " + result.getMove());
            return result.getMove();
        } else {
            // System.out.println("No valid move found.");
            return null;
        }
    }

    public AlphaBetaResult alphaBeta(GameState state, GameState prevState, double alpha, double beta, int depth, GameEngine ge, Move currentMove) {
        GameEngine ge2 = new GameEngine();

        if (depth == 0) {
            String playerColor = state.getCurrentColor().toLowerCase().equals("white")?"black":"white";
            double evaluation = evaluate(state, prevState, ge, prevState.getCurrentColor(), currentMove);

            return new AlphaBetaResult(evaluation, null);
        }

        boolean isMaximizingPlayer = state.getCurrentColor().toLowerCase().equalsIgnoreCase(super.getColor().toLowerCase());

        double value = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;




        Move bestMove = null;

        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.getCurrentColor().toLowerCase());

        HashMap<Vertex, ArrayList<Move>> vertexMove = ge2.getAllMovesFromAllPositions(allRingPositions, state.getGameBoard());


        HashMap<Vertex, ArrayList<Move>> validMoves = filterValidMoves(vertexMove, state);

//        List<Move> sortedMoves = sortMoves(validMoves, state, prevState, ge);

        // M A C H I N E    L E A R N I N G

//        HashMap<Vertex, ArrayList<Move>> validMoves = vertexMove;

        weights.evaluateAndAdjust(validMoves,state);

        List<Move> sortedMoves = weights.selectBestMove(validMoves,state);

        // M A C H I N E    L E A R N I N G


        if (validMoves.isEmpty()) {
            // System.out.println("No valid moves at depth=" + depth);

            return new AlphaBetaResult(evaluate(state, prevState, ge, prevState.getCurrentColor().toLowerCase(), currentMove), null);
        }

        int i = 0;

        for (Move m :sortedMoves) {

            GameState newState = moveState(state.clone(), m);

            if(!switched){

                newState.switchPlayerNEW();

            }

            double reward = Reward.calculateRewardWITHOUT(ge, state.clone(), m, newState.clone(), state.getCurrentColor().toLowerCase());
            m.setReward(reward);

            AlphaBetaResult result = alphaBeta(newState, state, alpha, beta, depth - 1, ge, m);
            double currentValue = result.getValue();


            if (isMaximizingPlayer) {
                if (currentValue > value) {
                    value = currentValue;
                    bestMove = m;

                }
                if (value > alpha) {
                    alpha = value;

                }
                if (alpha > beta) {

                    break;
                }
            } else {
                if (currentValue < value) {
                    value = currentValue;
                    bestMove = m;

                }
                if (value < beta) {
                    beta = value;

                }
                if (alpha > beta) {

                    break;
                }
            }
        }

        return new AlphaBetaResult(value, bestMove);
    }


    private HashMap<Vertex, ArrayList<Move>> filterValidMoves(HashMap<Vertex, ArrayList<Move>> vertexMove, GameState state) {
        HashMap<Vertex, ArrayList<Move>> validMoves = new HashMap<>();
        GameEngine ge = new GameEngine();

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
                    double value1 = evaluate(moveState(state, m1), state, ge, state.getCurrentColor(), m1);
                    double value2 = evaluate(moveState(state, m2), state, ge, state.getCurrentColor(), m2);
                    return Double.compare(value2, value1);
                });

                ArrayList<Move> topMoves = new ArrayList<>(validMovesForVertex.subList(0, Math.min(1, validMovesForVertex.size())));
                validMoves.put(entry.getKey(), topMoves);
            }
        }

        return validMoves;
    }






    private double evaluate(GameState state,GameState prevState, GameEngine ge, String color, Move chosenMove) {
        if (state == null || prevState == null || chosenMove == null) {
            // System.out.println("Invalid inputs to evaluate: state=" + state + ", prevState=" + prevState + ", chosenMove=" + chosenMove);
            return Double.NEGATIVE_INFINITY;
        }

        double valuation = Reward.calculateRewardWITHOUT(ge, prevState, chosenMove, state, color);
        if (Double.isInfinite(valuation) || Double.isNaN(valuation)) {
            // System.out.println("Error in evaluation: value is invalid. Returning fallback value.");
            return 0;

        }
        return valuation;
    }

    private GameState moveState(GameState state, Move move) {
        GameState newState = state.clone();

        Game_Board board = newState.getGameBoard();

        String thisBotColor = super.getColor().toLowerCase().equals("white")? "white":"black";
        Bot thisBot = new com.ken2_1.bots.AlphaBetaBot.AlphaBetaBot(thisBotColor);
        Bot opponentBot = new com.ken2_1.bots.AlphaBetaBot.AlphaBetaBot(thisBotColor.toLowerCase().equals("white")?"black":"white");

        String currentPlayerColor="";

        currentPlayerColor = state.currentPlayerColor().toLowerCase().equals("white") ? "white" : "black";

        GameEngine tempEngine = new GameEngine();
        tempEngine.currentState = newState.clone();

        Vertex fromVertex = move.getStartingVertex();

        if (fromVertex == null) {
            // System.out.println("Error: Source vertex is null.");
            return state;
        }

        int fromIndex = fromVertex.getVertextNumber();
        int toIndex = board.getVertexNumberFromPosition(
                move.getXposition(),
                move.getYposition()
        );

        if (toIndex == -1) {
            // System.out.println("Error: Invalid target vertex index.");
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
            // System.out.println("sourceV is null. : ");

            return state;
        }

        Ring ringToMove;
        if (sourceV.hasRing()) {
            ringToMove = (Ring) sourceV.getRing();
            sourceV.setRing(null);
        } else {
            // System.out.println("hasRing is null. CurrentBot: " );

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
            // System.out.println("targetV is null. CurrentBot: ");

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
            // System.out.println("REMOVING RING for "+tempEngine.getWinningColor());
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
                    // System.out.println("CHANGING COLOR BEFORE"+gameEngine.currentState.isWhiteTurn);
                    // switchTurn(gameEngine.currentState);
                    // System.out.println("CHANGING COLOR AFTER"+gameEngine.currentState.isWhiteTurn);

                    // this.switched = true;
                    // gameEngine.currentState.switchPlayerNEW();
                }
            }
        }
    }

    private void switchTurn(GameState state) {
        // state.switchPlayerNEW();
        state.isWhiteTurn = !state.isWhiteTurn;
    }

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

    public String getName() {
        return "AlphaBeta";
    }


}
