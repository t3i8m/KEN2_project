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
            System.out.println("No free positions available on the board.");
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
        } else {
            ArrayList<Vertex> coordinatesOfTheRings = board.getVertexAllOfVertexesOfSpecificColour(super.getColor());
            if (coordinatesOfTheRings == null || coordinatesOfTheRings.isEmpty()) {
                System.out.println("No rings available for the color: " + super.getColor());
                return null;
            }

            Vertex startingRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));
            GameSimulation gs = new GameSimulation();
            Move simulatedMove = gs.simulateMove(board, startingRing, targetVertex); 
            if (simulatedMove == null) {
                simulatedMove.setStartingVertex(startingRing);
                System.out.println("Simulation of the move has returned null. Check the GameSimulation class.");
            } else {
                return simulatedMove;
            }
        }
        return null;
    }
}
