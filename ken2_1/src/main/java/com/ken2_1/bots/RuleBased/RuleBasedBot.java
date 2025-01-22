package com.ken2_1.bots.RuleBased;

import com.ken2_1.engine.GameSimulation;
import com.ken2_1.engine.GameState;
import com.ken2_1.engine.Move;
import java.util.ArrayList;
import java.util.Random;
import com.ken2_1.Game_Components.Board.*;
import com.ken2_1.bots.BotAbstract;

/**
 * A Rule-Based Bot implementation for decision-making in a game.
 * The bot places rings or makes moves based on a simple set of rules,
 * often choosing moves randomly from valid options.
 */
public class RuleBasedBot extends BotAbstract {

    /**
     * Constructor to initialize the bot with its color.
     *
     * @param color The color of the bot ("white" or "black").
     */
    public RuleBasedBot(String color){
        super(color);
    }

    /**
     * Decides the next move for the bot based on the current game state.
     *
     * @param state The current game state.
     * @return The move to be executed by the bot.
     */
    public Move makeMove(GameState state){
        Game_Board board = state.getGameBoard(); 
        Random random = new Random();

        ArrayList<Vertex> allFreePositions = board.getAllFreeVertexes();

        if (allFreePositions.isEmpty()) {
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
                    return null;
                }

                GameSimulation gs = new GameSimulation();
                Vertex startingRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));
                Move simulatedMove = selectRandomMove(gs, startingRing, board);
                int vertexTo = board.getVertexNumberFromPosition(simulatedMove.getXposition(), simulatedMove.getYposition());

               
                if (simulatedMove == null || vertexTo<0) {
                } else {
                    work=false;
                    simulatedMove.setStartingVertex(startingRing);
                    return simulatedMove;
                }
            }
            
        }
        return null;
    }

    /**
     * Simulates and selects a random move for the given ring.
     *
     * @param gs            The game simulation object.
     * @param startingRing  The vertex where the ring to be moved is located.
     * @param board         The current game board.
     * @return A randomly selected valid move.
     */
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

    /**
     * Gets the name of the bot.
     *
     * @return The bot's name ("RuleBased").
     */
    @Override
    public String getName() {
        return "RuleBased";
    }
}
