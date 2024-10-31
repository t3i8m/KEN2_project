package com.ken2.bots.RuleBased;

import com.ken2.engine.GameSimulation;
import com.ken2.engine.Move;
import java.util.ArrayList;
import java.util.Random;
import com.ken2.Game_Components.Board.*;


public class RuleBasedBot {
    private String colour;


    public RuleBasedBot(String colour){
        this.colour = colour;
    }

    public Move makeMove(Game_Board board, boolean startingRingPlacement){
        Random random = new Random();
        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();
        Vertex targetVertex = allFreePositions.get(random.nextInt(allFreePositions.size()));

        if(startingRingPlacement){
            // placing starting ring
            int vertexNumber = targetVertex.getVertextNumber();
            int[] targetPosition = board.getVertexPositionByNumber(vertexNumber);
            PlayObj ring = new Ring(this.colour);
            board.updateBoard(vertexNumber, ring);
            return new Move(targetPosition[0], targetPosition[1], null);
        }else{
            // determining the start position and the make a move to the target position 
            ArrayList<Vertex> coordinatesOfTheRings = board.getVertexAllOfVertexesOfSpecificColour(this.colour);
            Vertex startingRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));

            GameSimulation gs = new GameSimulation();
            Move simulatedMove = gs.simulateMove(board, startingRing, targetVertex);
            if(simulatedMove!=null){
                System.out.println("Simulation of the move has returned null check the GameSimulation.java");
            }else{
                return simulatedMove;
            }
        }
        return null;
    }
}
