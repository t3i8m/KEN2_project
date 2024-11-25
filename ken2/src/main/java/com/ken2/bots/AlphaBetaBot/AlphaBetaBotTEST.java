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


public class AlphaBetaBotTEST extends BotAbstract{

    public AlphaBetaBotTEST(String color){
        super(color);
    }

    public Move makeMove(GameState state){
        int depth = 3;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        GameState stateClone = state.clone(); //clone pattern (prototype)
        GameEngine ge = new GameEngine();

        // new
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
        // new
        AlphaBetaResult result= alphaBeta(stateClone, alpha, beta, depth, ge);

        if (result != null && result.getMove() != null) {
            // System.out.println("Bro executed move: " + result.getMove());
            return result.getMove();
        } else {
            // System.out.println("wtf, no valid move found.");
            return null;
        }
    }


    public AlphaBetaResult alphaBeta(GameState state, double alpha, double beta, int depth, GameEngine ge){
        if (depth==0){
            return new AlphaBetaResult(evaluate(state,ge, state.currentPlayerColor()), null); //TODO: evaluation function takes state-> returns double (Nikhil)
        }

        Move bestMove = null;

        if(state.currentPlayerColor().equals(this.getColor())){
            double value = Double.NEGATIVE_INFINITY;

            ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());  // all of the this colour ring positions
            HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions); // for each vertex list of all possible moves

            // iterate over the hashmap of vertex:array<Moves>
            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                Vertex key = entry.getKey();
                ArrayList<Move> possibleMovesFromThisVertex = entry.getValue();

                // iterate over all the possible moves for this vertex
                for (Move m : possibleMovesFromThisVertex) {
                    // here we need to simulate a move and get a new state

                    GameState newState = moveState(state,m);
                    //TODO: make a result function takes (state, move)-> new_state (Lera davaj)

                    AlphaBetaResult result = alphaBeta(newState, alpha, beta, depth - 1, ge);

                    if (result.getValue() > value) {
                        value = result.getValue();
                        bestMove = m;
                    }
                    alpha = Math.max(alpha, value); // maximizer 
                    if (alpha >= beta) break; // alpha prune

                }
               
            }
            return new AlphaBetaResult(value, bestMove);

        } else{
            // TODO: same for the black
        }
        return null;
    }

    /**
     * Gives a given state a value for pruning later
     * @param state current game state
     * @param ge game engine
     * @param color current player color
     * @return value of the function
     */
    private double evaluate(GameState state, GameEngine ge, String color){
        double valuation = 0;
        String opponentColor = color.equals("white") ? "black" : "white";

        /** Purpose of the variables below
         * number of coins of our colour
         * number of coins in a row of our colour

         * number of coins of opposite colour
         * number of coins in a row of opposite colour

         * Same with the rings

         * if we will get a win if we move
         * Then bonus points will be awarded LESSGOOOOO
         */
        double inOurfavour = 0.50;
        double notOurfavour= -0.25;
        double ourWin = 1;
        double theirWin = -1;

        // Calculating the chip calculation
        valuation += state.getChipsCountForColor(color)*inOurfavour + state.getChipsCountForColor(opponentColor)*notOurfavour;

        // Calculating the ring calculation
        valuation += state.getRingCountForColor(color)*inOurfavour + state.getRingCountForColor(opponentColor)*notOurfavour;

        // Calculating the win calculation
        valuation += ge.winningColor(ge.currentState.getVertexesOfFlippedCoins()).equals(color)? ourWin : theirWin;

        return valuation;
    }


    private GameState moveState(GameState state, Move move){
        GameEngine tempEngine = new GameEngine();
        tempEngine.currentState=state.clone();
        int toVertex = tempEngine.currentState.gameBoard.getVertexNumberFromPosition(move.getXposition(),move.getXposition());

        move = tempEngine.gameSimulation.simulateMove(tempEngine.gameBoard,move.getStartingVertex(),new Vertex(move.getXposition(),move.getYposition()));

        if (tempEngine.currentState.ringsPlaced<10){
            //Place rings
            tempEngine.placeStartingRing(toVertex, state.currentPlayerColor());
        }
        else if (toVertex>0){
            //Place chips
            // System.out.println("A-B CHIPS PLACE TESTING PRINTY: "+toVertex+" ");
            if (!tempEngine.currentState.gameBoard.getVertex(toVertex).hasRing() &
                ! tempEngine.currentState.gameBoard.getVertex(toVertex).hasCoin()) {

                // Basically checking if the toVertex is empty AND is starting vertex is not empty

                int[] moveValid = {1};
                if (move != null) {

                    tempEngine.placeChip(move.getStartingVertex().getVertextNumber());

                    // flip coins
                    ArrayList<Coin> coinsToFlip = move.getFlippedCoins();
                    ArrayList<Vertex> flippedVertices = new ArrayList<>();
                    if (!coinsToFlip.isEmpty()) {
                        for (Coin c : coinsToFlip) {
                            flippedVertices.add(tempEngine.currentState.gameBoard.getVertexByCoin(c));
                        }
                    }
                    tempEngine.currentState.setVertexesOfFlippedCoins(flippedVertices);
                }
                tempEngine.currentState.chipRingVertex = -1;
                tempEngine.currentState.chipsRemaining -= 1;
                tempEngine.currentState.chipPlaced = false;
                tempEngine.currentState.selectedRingVertex = -1;
                tempEngine.currentState.updateChipsRingCountForEach();
            }
        }
        // else System.out.println("LOLZZZ");

        return tempEngine.currentState;
    }


}