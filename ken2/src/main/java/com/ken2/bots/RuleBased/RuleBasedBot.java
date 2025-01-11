package com.ken2.bots.RuleBased;

import com.ken2.engine.GameSimulation;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import java.util.ArrayList;
import java.util.Random;
import com.ken2.Game_Components.Board.*;
import com.ken2.bots.BotAbstract;

public class RuleBasedBot extends BotAbstract {

    public RuleBasedBot(String color){
        super(color);
    }


    public Move makeMove(GameState state){
        Game_Board board = state.getGameBoard(); 
        Random random = new Random();

        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
            // System.out.println("No free positions available on the board.");
            return null;
        }
        
        Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size()));

        if (state.ringsPlaced < 10) { 
            int vertexNumber = targetVertex.getVertextNumber();
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(super.getColor());
            board.updateBoard(vertexNumber, ring);
            state.ringsPlaced++;  
            return new Move(targetPosition[0], targetPosition[1], null);
        }
        else {
            boolean work = true;
            while(work){
                ArrayList<Vertex> coordinatesOfTheRings = state.getAllVertexOfColor(super.getColor());
                if (coordinatesOfTheRings == null || coordinatesOfTheRings.isEmpty()) {
                    // System.out.println("No rings available for the color: " + super.getColor());
                    return null;
                }

                GameSimulation gs = new GameSimulation();
                Vertex startingRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));
                Move simulatedMove = selectRandomMove(gs, startingRing, board);
                int vertexTo = board.getVertexNumberFromPosition(simulatedMove.getXposition(), simulatedMove.getYposition());

                // System.out.println("STARTING RING "+startingRing.getVertextNumber());
                // System.out.println("SIMULATED MOVE X "+simulatedMove.getXposition());
                // System.out.println("SIMULATED MOVE Y "+simulatedMove.getYposition());

                // Move simulatedMove = gs.simulateMove(board, startingRing, ); 
                if (simulatedMove == null || vertexTo<0) {
                    // System.out.println("Simulation of the move has returned null. Check the GameSimulation class.");
                } else {
                    work=false;
                    simulatedMove.setStartingVertex(startingRing);
                    return simulatedMove;
                }
            }
            
        }
        return null;
    }

    public Move selectRandomMove(GameSimulation gs, Vertex startingRing, Game_Board board){
        gs.startSimulation(board.getBoard().clone(), startingRing.getXposition(), startingRing.getYposition());

        ArrayList<ArrayList<Move>> possibleMoves = gs.getAllPossibleMoves();

        ArrayList<Move> allMoves = new ArrayList<>();

        for (ArrayList<Move> moveList : possibleMoves) {
            if(moveList.isEmpty()){
                continue;
            }
            allMoves.addAll(moveList);
        }

        if (allMoves.isEmpty()) {
            // System.out.println("No possible moves available.");
            return null; 
        }

        Random random = new Random();
        Move randomMove = allMoves.get(random.nextInt(allMoves.size()));
        return randomMove;
    }


    @Override
    public String getName() {
        return "RuleBased";
    }
}
