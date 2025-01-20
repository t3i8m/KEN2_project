package com.ken2.bots.AlphaBetaBot;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean switched = false;
    private int[] zoneA = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 19, 20, 21, 28, 29, 30, 38 };
    private int[] zoneB = { 9, 10, 17, 18, 25, 26, 27, 35, 36, 37, 45, 46, 54, 55, 56, 63, 64, 65, 71, 72, 73, 80 };
    private int[] zoneC = { 39, 47, 48, 49, 57, 58, 59, 66, 67, 68, 69, 70, 74, 75, 76, 77, 78, 79, 81, 82, 83, 84 };
    private int[] zoneD = { 22, 23, 24, 31, 32, 33, 34, 40, 41, 42, 43, 44, 50, 51, 52, 53, 60, 61, 62 };
    


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

        if (state.ringsPlaced < 10) {    int vertexNumber = allFreePositions.get(random.nextInt(allFreePositions.size())).getVertextNumber();

            // check how many rings are in each zone
            //white is + black is -
            int scoreA = calculateZoneScore(board, 0);
            int scoreB = calculateZoneScore(board, 0);
            int scoreC = calculateZoneScore(board, 0);
            int scoreD = calculateZoneScore(board, 0);

            //flip scores based on player color
            if(StateRightNow.isWhiteTurn){
                scoreA = -scoreA;
                scoreB = -scoreB;
                scoreC = -scoreC;
                scoreD = -scoreD;
            }

            // check choak points of each zone 
            ArrayList<Integer> freeChoakPoints = new ArrayList<>(
                Arrays.asList(5,9,38,46,75,79)
            );

            for(int i = 0 ; i < freeChoakPoints.size(); i++){
                vertexNumber = freeChoakPoints.get(i);
                if(!board.getVertex(vertexNumber).hasRing()){
                int[] targetPosition = makePosition(state, board, vertexNumber);
                return new Move(targetPosition[0], targetPosition[1], null);   
                }
            }

            // see if any score is less than -2. if so, selecet that zone

            if(scoreA < -2){
                boolean keepLooking = true;
                while (keepLooking) {
                    vertexNumber = zoneA[random.nextInt(zoneA.length)];
                    if(!board.getVertex(vertexNumber).hasRing()){
                        keepLooking = false;
                    }
                }
                int[] targetPosition = makePosition(state, board, vertexNumber);
                return new Move(targetPosition[0], targetPosition[1], null);
            }
            if(scoreB < -2){
                boolean keepLooking = true;
                while (keepLooking) {
                    vertexNumber = zoneB[random.nextInt(zoneB.length)];
                    if(!board.getVertex(vertexNumber).hasRing()){
                        keepLooking = false;
                    }
                }
                int[] targetPosition = makePosition(state, board, vertexNumber);
                return new Move(targetPosition[0], targetPosition[1], null);
            }

            if(scoreC < -2){
                boolean keepLooking = true;
                while (keepLooking) {
                    vertexNumber = zoneC[random.nextInt(zoneC.length)];
                    if(!board.getVertex(vertexNumber).hasRing()){
                        keepLooking = false;
                    }
                }
                int[] targetPosition = makePosition(state, board, vertexNumber);
                return new Move(targetPosition[0], targetPosition[1], null);
            }

            if(scoreD < -2){
                boolean keepLooking = true;
                while (keepLooking) {
                    vertexNumber = zoneD[random.nextInt(zoneD.length)];
                    if(!board.getVertex(vertexNumber).hasRing()){
                        keepLooking = false;
                    }
                }
                int[] targetPosition = makePosition(state, board, vertexNumber);
                return new Move(targetPosition[0], targetPosition[1], null);
            }

            vertexNumber = allFreePositions.get(random.nextInt(allFreePositions.size())).getVertextNumber();
            int[] targetPosition = makePosition(state, board, vertexNumber);
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

    
    private int[] makePosition(GameState state, Game_Board board, int vertexNumber) {
        int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
        PlayObj ring = new Ring(super.getColor());
        board.updateBoard(vertexNumber, ring);
        state.ringsPlaced++;
        return targetPosition;
    }

    private int calculateZoneScore(Game_Board board, int score) {
        for(int i=0; i < zoneA.length; i++){
            Vertex selected = board.getVertex(zoneA[i]);
            if(selected.hasRing()){
                PlayObj ring = selected.getRing();
                if (ring.getColour().equalsIgnoreCase("White")){
                    score++;
                }else {
                    score--;
                }
            }
        }
        return score;
    }

    public AlphaBetaResult alphaBeta(GameState state, GameState prevState, double alpha, double beta, int depth, GameEngine ge, Move currentMove) {
        GameEngine ge2 = new GameEngine();
        // System.out.println(state.getCurrentColor());
        if (depth == 0) {
            String playerColor = state.getCurrentColor().toLowerCase().equals("white")?"black":"white"; 
            double evaluation = evaluate(state, prevState, ge, prevState.getCurrentColor(), currentMove);
            // System.out.println("Depth=0 | Evaluation: " + evaluation + " | Move: " + currentMove+" color"+prevState.getCurrentColor());

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
        List<Move> sortedMoves = sortMoves(validMoves, state, prevState, ge);

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
        // for (Map.Entry<Vertex, ArrayList<Move>> entry : validMoves.entrySet()) {
        //     i++;
        for (Move m :sortedMoves) {
            // System.out.println(i);
            // System.out.println("Before alphaBeta: Depth=" + depth +
            // ", Alpha=" + alpha + ", Beta=" + beta +
            // ", CurrentPlayer=" + state.getCurrentColor());

            GameState newState = moveState(state.clone(), m);
            // if(!switched){

            // }
            // if (newState.getRingCountForColor(newState.getCurrentColor().toLowerCase()) < (state.getRingCountForColor(newState.getCurrentColor().toLowerCase()) )) { 
            //     m.setReward(Double.POSITIVE_INFINITY);
            //     newState.switchPlayerNEW();

            //     System.out.println("Winning move found at depth=" + depth + ": " + m);
            //     return new AlphaBetaResult(Double.POSITIVE_INFINITY, m); 
            // }
            if(!switched){

                newState.switchPlayerNEW();

            }


            // newState.isWhiteTurn = !state.isWhiteTurn;

            double reward = Reward.calculateRewardWITHOUT(ge, state.clone(), m, newState.clone(), state.getCurrentColor().toLowerCase());
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
            
            // System.out.println("Depth=" + depth + ", Current Move=" + m + ", Current Value=" + currentValue + ", Value=" + value);

            // System.out.println("After Iteration: Depth=" + depth + ", Alpha=" + alpha + ", Beta=" + beta + ", Value=" + value);
            // System.out.println("Depth=" + depth + ", Alpha=" + alpha + ", Beta=" + beta + ", Value=" + value);
        }
    // }
    
        return new AlphaBetaResult(value, bestMove);
    }

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

        switched = false;

        if (isWinningRow) {
            // System.out.println("REMOVING RING for "+tempEngine.getWinningColor());
            String winnerColor = tempEngine.getWinningColor();
            Bot currentPlayer = winnerColor.equalsIgnoreCase(super.getColor()) ? thisBot : opponentBot;

            Vertex vertexToRemoveBOT = currentPlayer.removeRing(tempEngine.currentState);
            String vertexColor = vertexToRemoveBOT.getRing().getColour();
            handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);
            // handleWinningRing(vertexToRemoveBOT.getVertextNumber(), tempEngine);

            ArrayList<Integer> allRemoveChips = currentPlayer.removeChips(tempEngine.currentState);
            for (Integer vert : allRemoveChips) {
                handleChipRemove(vert, tempEngine, currentPlayer);
            }
            tempEngine.setWinningRing(false);
            tempEngine.setChipRemovalMode(false);
            tempEngine.currentState.setVertexesOfFlippedCoins(null);

            // if(vertexColor.equals(tempEngine.currentState.getCurrentColor().toLowerCase())){
            //     tempEngine.currentState.switchPlayerNEW();
            //     this.switched = true;
            // }

            tempEngine.currentState.updateChipsRingCountForEach();
            tempEngine.setChipRemovalMode(false);
            tempEngine.setRingSelectionMode(false);
            tempEngine.setWinningColor("");
            winningChips.clear();
            tempEngine.setWinningRing(false);
            tempEngine.setChipRemovalMode(false);
            tempEngine.getWinningChips().clear();
            // System.out.println("CHANGING COLOR RESULT"+tempEngine.currentState.isWhiteTurn);


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
