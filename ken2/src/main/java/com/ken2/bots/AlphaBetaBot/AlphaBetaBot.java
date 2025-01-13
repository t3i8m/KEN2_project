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

public class AlphaBetaBot extends BotAbstract {
    private GameState StateRightNow;
    int[][] verticalls = {
        {4,5,6,7,8,9,10},
        {11,12,13,14,15,16,18},
        {19,20,21,22,23,24,25,26,27},
        {28,29,30,31,32,33,34,35,36,37},
        {38,39,40,41,42,43,44,45,46},
        {47,48,49,50,51,52,53,54,55,56},
        {57,58,59,60,61,62,63,64,65},
        {66,67,68,69,70,71,72,73},
        {74,75,76,77,78,79,80},
    };
    int[][] slopeDown ={
        {28,38,48,58,67,75,81},
        {19,29,39,49,59,68,76,82},
        {11,20,30,40,50,60,69,77,83},
        {4,12,21,31,41,51,61,70,78,84},
        {5,13,22,32,42,52,62,71,79},
        {0,6,14,23,33,43,53,63,72,80},
        {1,7,15,24,34,44,54,64,73},
        {2,8,16,25,35,45,55,65},
        {3,9,17,26,36,46,56},
    };
    int[][] slopeUp ={
        {0,5,12,20,29,38,47},
        {1,6,13,21,30,39,48,57},
        {2,7,14,22,31,40,49,58,66},
        {3,8,15,23,32,41,50,59,67,74},
        {9,16,24,33,42,51,60,68,75},
        {10,17,25,34,43,52,61,69,76,81},
        {18,26,35,44,53,62,70,77,82},
        {27,36,45,54,63,71,78,83},
        {37,46,55,64,72,79,84}, 
    };

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

        AlphaBetaResult result = alphaBeta(this.StateRightNow, alpha, beta,3, ge);

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
        // System.out.println(allRingPositions);
        HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions, state.gameBoard);
        GameSimulation gs = new GameSimulation();
        
        if (vertexMove.isEmpty()) {
            return new AlphaBetaResult(evaluate(state, ge, state.currentPlayerColor()), null);
        }

        if (state.currentPlayerColor().equalsIgnoreCase(this.getColor())) {
            value = Double.NEGATIVE_INFINITY;

            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                for (Move m : entry.getValue()) {
                    if(entry==null){
                        continue;
                    }

                    int vertexFrom = m.getStartingVertex().getVertextNumber();
                    int vertexTo = state.gameBoard.getVertexNumberFromPosition(m.getXposition(), m.getYposition());

                    if (!isValidMove(state, m)) {
                        continue; 
                    }
                    if(vertexFrom<0 || vertexTo<0){
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
                    if(entry==null){
                        continue;
                    }
                    int vertexFrom = m.getStartingVertex().getVertextNumber();
                    int vertexTo = state.gameBoard.getVertexNumberFromPosition(m.getXposition(), m.getYposition());

                    if (!isValidMove(state, m)) {
                        continue; 
                    }
                    if(vertexFrom<0 || vertexTo<0){
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

        // valuation += state.getChipsCountForColor(color) * inOurfavour
        //         + state.getChipsCountForColor(opponentColor) * notOurfavour;

        // valuation += state.getRingCountForColor(color) * inOurfavour
        //         + state.getRingCountForColor(opponentColor) * notOurfavour;

        valuation += ge.winningColor(state.getVertexesOfFlippedCoins()).equals(color) ? ourWin : theirWin;

        ArrayList<Vertex> vertices = state.getAllVertexOfColor(color);
        ArrayList<Vertex> ringless = new ArrayList<>();
        for(Vertex v: vertices){
            if(v.hasCoin()){
                ringless.add(v);
            }
        }


        ArrayList<Vertex> opponentVertices = state.getAllVertexOfColor(opponentColor);
        ArrayList<Vertex> opponentRingless = new ArrayList<>();

        for(Vertex v: opponentVertices){
            if(v.hasCoin()){
                opponentRingless.add(v);
            }
        }
        // one away a win
        boolean[][] verticalSelections = makeSelectionArray(verticalls, ringless);
        boolean[][] downSelections = makeSelectionArray(slopeDown, ringless);
        boolean[][] upSelections = makeSelectionArray(slopeUp, ringless);

        valuation += checkIfAway(upSelections);
        valuation += checkIfAway(downSelections);
        valuation += checkIfAway(verticalSelections);

        // density
        // double density = density(upSelections);
        // density += density(downSelections);
        // density += density(verticalSelections);

        // valuation += density*0.1;
        

        // one away a loss

        verticalSelections = makeSelectionArray(verticalls, opponentRingless);
        downSelections = makeSelectionArray(slopeDown, opponentRingless);
        upSelections = makeSelectionArray(slopeUp, opponentRingless);

        valuation -= 0.2*checkIfAway(upSelections);
        valuation -= 0.2*checkIfAway(downSelections);
        valuation -= 0.2*checkIfAway(verticalSelections);
        

        return valuation;
    }

    // private int density(boolean[][] selections){
    //     int highestTotal = 0;

    //     for (int i = 1; i < (selections.length - 1);i++){
    //         int total = 0;
    //         for (int j = 0; j < selections[i-1].length; j++){
    //             if(selections[i-1][j]){
    //                 total++;
    //             }
    //         }
    //         for (int j = 0; j < selections[i].length; j++){
    //             if(selections[i][j]){
    //                 total++;
    //             }
    //         }

    //         for (int j = 0; j < selections[i+1].length; j++){
    //             if(selections[i+1][j]){
    //                 total++;
    //             }
    //         }

    //         if(total > highestTotal){
    //             highestTotal = total;
    //         }
    //     }
    //     return highestTotal;
    // }

    private double checkIfAway(boolean[][] selections){
        double valuation = 0;
        for(int i=0; i < selections.length; i++){
            if(numberAwayToWin(selections[i], 0, 5)){
                valuation += 15;
            }
            if(numberAwayToWin(selections[i], 1, 5)){
                valuation += 8;
            }

            if(numberAwayToWin(selections[i], 0, 3)){
                valuation +=4;
            }

            if(numberAwayToWin(selections[i], 0, 2)){
                valuation +=1;
            }
        }
        return valuation;
    }
    private boolean numberAwayToWin(boolean[] selectionArray, int awayTo, int inARow) {

        for (int i = 0; i <= selectionArray.length - 5; i++) {
            int countTrue = 0;

            for (int j = i; j < i + inARow; j++) {
                if (selectionArray[j] == true) {
                    countTrue++;
                } 
            }

            if (countTrue == inARow-awayTo) {
                return true;
            }
        }
        return false;
    }


    private boolean[][] makeSelectionArray(int[][] rows, ArrayList<Vertex> vertices){

        int[] filled = new int[vertices.size()];
        
        for(int i=0;i<vertices.size(); i++){
           filled[i] = vertices.get(i).getVertextNumber();
        }

        boolean[][] selection = new boolean[rows.length][];

        for(int i = 0; i < rows.length; i++){
            selection[i] = new boolean[rows[i].length];
            for(int j = 0; j<rows[i].length; j++){
                selection[i][j] = contains(filled, rows[i][j]);
            }
        }     

        return selection;
    }

    public boolean contains(int[] array, int target) {
        for (int value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
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
    
        PlayObj ring = (Ring)startVertex.getRing();
    
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

    public String getName() {
        return "AlphaBeta";
    }
    
    
}
