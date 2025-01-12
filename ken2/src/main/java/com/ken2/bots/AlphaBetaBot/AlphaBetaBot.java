package com.ken2.bots.AlphaBetaBot;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ken2.Game_Components.Board.*;
import com.ken2.bots.Bot;
import com.ken2.bots.BotAbstract;
import com.ken2.bots.DQN_BOT_ML.utils.Reward;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;

import javafx.scene.control.skin.TextInputControlSkin.Direction;

public class AlphaBetaBot extends BotAbstract {
    private GameState StateRightNow;
    private int chipsToRemove;
    private List<Integer> winningChips = new ArrayList<>();

    public AlphaBetaBot(String color) {
        super(color);
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
        StateRightNow.setCurrentPlayer(super.getColor());
        AlphaBetaResult result = alphaBeta(this.StateRightNow,this.StateRightNow, alpha, beta, 2, ge, null);

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
        // System.out.println(state.getCurrentColor());

        if (depth == 0) {
            String playerColor = state.getCurrentColor().toLowerCase().equals("white")?"black":"white"; 
            double evaluation = evaluate(state, prevState, ge, prevState.getCurrentColor(), currentMove);
            // System.out.println("Depth=0 | Evaluation: " + evaluation + " | Move: " + currentMove);

            // System.out.println("Terminal depth reached. Evaluation: " + evaluation + " for move: " + currentMove+" color"+playerColor);
            return new AlphaBetaResult(evaluation, null);
        }
        boolean isMaximizingPlayer = state.getCurrentColor().toLowerCase().equalsIgnoreCase(super.getColor().toLowerCase());

        // System.out.println(prevState.getCurrentColor());
        // System.out.println(super.getColor());
        // System.out.println(isMaximizingPlayer);
        double value = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

    
        Move bestMove = null;
    
        ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.getCurrentColor().toLowerCase());
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge2.getAllMovesFromAllPositions(allRingPositions, state.getGameBoard());
        HashMap<Vertex, ArrayList<Move>> validMoves = filterValidMoves(vertexMove, state);
        // System.out.println(state.getGameBoard().strMaker());

        // System.out.println("Valid moves count: " + validMoves.size() + " at depth=" + depth);
    
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves at depth=" + depth);
            
            return new AlphaBetaResult(evaluate(state, prevState, ge, prevState.getCurrentColor().toLowerCase(), currentMove), null);
        }
        // System.out.println(state.getCurrentColor());
        // System.out.println(prevState.getCurrentColor());
        // System.out.println(super.getColor());
        // System.out.println(isMaximizingPlayer);
        // if(isMaximizingPlayer){
        //     state.setCurrentPlayer(super.getColor().toLowerCase());
        // } else{
        //     state.setCurrentPlayer(super.getColor().toLowerCase().equals("white")?"black":"white");

        // }
        int i = 0;
        for (Map.Entry<Vertex, ArrayList<Move>> entry : validMoves.entrySet()) {
            i++;
            for (Move m : entry.getValue()) {
                // System.out.println(i);
                // System.out.println("Before alphaBeta: Depth=" + depth +
                // ", Alpha=" + alpha + ", Beta=" + beta +
                // ", CurrentPlayer=" + state.getCurrentColor());

                GameState newState = moveState(state, m);
                newState.switchPlayer();
                // newState.setCurrentPlayer(
                //     state.getCurrentColor().toLowerCase().equals("white") ? "black" : "white"
                // );
                // System.out.println("After moveState: CurrentPlayer=" + newState.currentPlayerColor() + ", Depth=" + depth);


                // newState.isWhiteTurn = !state.isWhiteTurn;

                double reward = Reward.calculateRewardWITHOUT(ge, state, m, newState, state.getCurrentColor());
                m.setReward(reward);
                // System.out.println(m);
                // System.out.println(newState.getCurrentColor());



                // state.switchPlayer();
                // System.out.println("Entering alphaBeta: Depth=" + depth + 
                //    ", Alpha=" + alpha + 
                //    ", Beta=" + beta + 
                //    ", CurrentPlayer=" + state.getCurrentColor());

                AlphaBetaResult result = alphaBeta(newState, state, alpha, beta, depth - 1, ge, m);
                double currentValue = result.getValue();

                // System.out.println("Depth=" + depth +
                // " | CurrentPlayer=" + state.getCurrentColor() +
                // " | NextPlayer=" + newState.getCurrentColor());
            
                if (isMaximizingPlayer) {
                    if (currentValue > value) {
                        value = currentValue;
                        bestMove = m;
                        // System.out.println("Maximizing: New best value = " + value + " at move " + m);
                    }
                    if (value > alpha) {
                        alpha = value;
                        // System.out.println("Maximizing: Alpha updated to " + alpha);
                    }
                    if (alpha > beta) {
                        // System.out.println("Maximizing: Pruning at alpha = " + alpha + ", beta = " + beta);
                        break;
                    }
                } else {
                    if (currentValue < value) {
                        value = currentValue;
                        bestMove = m;
                        // System.out.println("Minimizing: New best value = " + value + " at move " + m);
                    }
                    if (value < beta) {
                        beta = value;
                        // System.out.println("Minimizing: Beta updated to " + beta);
                    }
                    if (alpha > beta) {
                        // System.out.println("Minimizing: Pruning at alpha = " + alpha + ", beta = " + beta);
                        break;
                    }
                }
                
                System.out.println("Depth=" + depth + ", Current Move=" + m + ", Current Value=" + currentValue + ", Value=" + value);

                // System.out.println("After Iteration: Depth=" + depth + ", Alpha=" + alpha + ", Beta=" + beta + ", Value=" + value);
                // System.out.println("Depth=" + depth + ", Alpha=" + alpha + ", Beta=" + beta + ", Value=" + value);
            }
        }
    
        return new AlphaBetaResult(value, bestMove);
    }
    

    private HashMap<Vertex, ArrayList<Move>> filterValidMoves(HashMap<Vertex, ArrayList<Move>> vertexMove, GameState state) {
        HashMap<Vertex, ArrayList<Move>> validMoves = new HashMap<>();
    
        for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
            if (entry == null || entry.getValue() == null) {
                continue; 
            }
    
            ArrayList<Move> validMovesForVertex = new ArrayList<>();
            for (Move m : entry.getValue()) {
                // System.out.println(m);
                if (isValidMove(state, m)) {
                    validMovesForVertex.add(m);
                }
            }
    
            if (!validMovesForVertex.isEmpty()) {
                validMoves.put(entry.getKey(), validMovesForVertex);
            }
        }
    
        return validMoves;
    }
    

    private double evaluate(GameState state,GameState prevState, GameEngine ge, String color, Move chosenMove) {
        if (state == null || prevState == null || chosenMove == null) {
            System.out.println("Invalid inputs to evaluate: state=" + state + ", prevState=" + prevState + ", chosenMove=" + chosenMove);
            return Double.NEGATIVE_INFINITY;
        }
        
        // double valuation = 0;
        // String opponentColor = color.equals("white") ? "black" : "white";

        // double inOurfavour = 0.5;
        // double notOurfavour = -0.25;
        // double ourWin = 1;
        // double theirWin = -1;

        // valuation += state.getChipsCountForColor(color) * inOurfavour
        //         + state.getChipsCountForColor(opponentColor) * notOurfavour;

        // valuation += state.getRingCountForColor(color) * inOurfavour
        //         + state.getRingCountForColor(opponentColor) * notOurfavour;

        // valuation += ge.winningColor(state.getVertexesOfFlippedCoins()).equals(color) ? ourWin : theirWin;
        double valuation = Reward.calculateRewardWITHOUT(ge, prevState, chosenMove, state, color);
        // System.out.println("Evaluation result: " + valuation + " for move: " + chosenMove);
        if (Double.isInfinite(valuation) || Double.isNaN(valuation)) {
            System.out.println("Error in evaluation: value is invalid. Returning fallback value.");
            return 0; 
        }
        return valuation;
    }

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

    
        if (isWinningRow) {
                        
            String winnerColor = tempEngine.getWinningColor();
            Bot currentPlayer = winnerColor.equalsIgnoreCase(super.getColor()) ? thisBot : opponentBot;

            Vertex vertexToRemoveBOT = currentPlayer.removeRing(tempEngine.currentState);
            
            handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);
            // handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);

            ArrayList<Integer> allRemoveChips = currentPlayer.removeChips(tempEngine.currentState);
            for (Integer vert : allRemoveChips) {
                handleChipRemove(vert, tempEngine, currentPlayer);
            }

            tempEngine.setWinningRing(false);
            tempEngine.setChipRemovalMode(false);


    
    
        }
        // tempEngine.currentState.setCurrentPlayer(
        //     tempEngine.currentState.currentPlayerColor().toLowerCase().equals("white") ? "black" : "white"
        // );
        // newState.setCurrentPlayer(
        //     state.currentPlayerColor().toLowerCase().equals("white") ? "black" : "white"
        // );
        // tempEngine.currentState.switchPlayerNEW();
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
        if (currColor.equalsIgnoreCase(gameEngine.getWinningColor())) {
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

                if (currColor.equals(activeBot.getColor().toLowerCase())) {
                    switchTurn(gameEngine.currentState);
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

    public String getName() {
        return "AlphaBeta";
    }
    
    
}
