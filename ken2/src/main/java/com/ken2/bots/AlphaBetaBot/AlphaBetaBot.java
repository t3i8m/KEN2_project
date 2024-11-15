package com.ken2.bots.AlphaBetaBot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.BotAbstract;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;


public class AlphaBetaBot extends BotAbstract{

    public AlphaBetaBot(String color){
        super(color);
    }

    public Move makeMove(GameState state){
        int depth = 3;
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        GameState stateClone = state.clone(); //clone pattern (prototype)
        GameEngine ge = new GameEngine();

        AlphaBetaResult result= alphaBeta(stateClone, alpha, beta, depth, ge);

        if (result != null && result.getMove() != null) {
            System.out.println("Bro executed move: " + result.getMove());
            return result.getMove();
        } else {
            System.out.println("wtf, no valid move found.");
            return null;
        }
    }


    public AlphaBetaResult alphaBeta(GameState state, double alpha, double beta, int depth, GameEngine ge){
        if (depth==0){
            //return new AlphaBetaResult(evaluate(state), null); //TODO: evaluation function takes state-> returns double (Nikhil)
        }

        Move bestMove = null;

        if(state.currentPlayerColor().equals("White")){
            double value = Double.NEGATIVE_INFINITY;

            ArrayList<Vertex> allRingPositions = state.getAllVertexOfColor(state.currentPlayerColor());  // all of the this colour ring positions
            HashMap<Vertex, ArrayList<Move>> vertexMove = ge.getAllMovesFromAllPositions(allRingPositions); // for each vertex list of all possible moves

            // itterate over the hashmap of vertex:array<Moves>
            for (Map.Entry<Vertex, ArrayList<Move>> entry : vertexMove.entrySet()) {
                Vertex key = entry.getKey();
                ArrayList<Move> possibleMovesFromThisVertex = entry.getValue();

                // itterate over all of the possible moves for this vertex
                for (Move m : possibleMovesFromThisVertex) {
                    // here we need to simulate a move and get a new state

                    GameState newState = null; //TODO: make a result function takes (state, move)-> new_state (Lera davaj)

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


}
