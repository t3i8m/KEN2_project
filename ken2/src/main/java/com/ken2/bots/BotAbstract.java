package com.ken2.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Direction;
import com.ken2.engine.GameState;

/**
 * Abstract class for all bots.
 * Implements common functionality and enforces a structure for derived bots.
 */
public abstract class BotAbstract implements Bot{
    private String color;

    /**
     * Constructor to initialize the bot's color.
     *
     * @param color The color of the bot ("white" or "black").
     */
    public BotAbstract(String color){
        this.color = color;
    }

    /**
     * Returns the color of the bot.
     *
     * @return The color ("white" or "black").
     */
    @Override
    public String getColor(){
        return this.color;
    }

    /**
     * Randomly selects a ring to remove from the board.
     * 
     * @param state The current game state.
     * @return The vertex where the ring is removed, or null if no rings are available.
     */
    @Override
    public Vertex removeRing(GameState state){
        Random random = new Random();
        ArrayList<Vertex> coordinatesOfTheRings = state.getAllVertexOfColor(this.getColor());
        if (coordinatesOfTheRings == null || coordinatesOfTheRings.isEmpty()) {
            return null;
        }
        Vertex potentialRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));

        return potentialRing;
    }

    /**
     * Randomly selects chips to remove from the board as part of the game's rules.
     * 
     * @param state The current game state.
     * @return A list of vertices where chips are removed, or null if not enough chips are available.
     */
    @Override
    public ArrayList<Integer> removeChips(GameState state) {
        ArrayList<Integer> chipsToRemove = new ArrayList<>();
        List <Integer> allChips = state.getAllPossibleCoinsToRemove();

        if(allChips.size()<5 ||allChips==null){
            return null;
        }

        for (Integer vert : allChips) {
             
            chipsToRemove.add(vert);
            if(chipsToRemove.size()==4){
                chipsToRemove.add(allChips.getLast());

                return chipsToRemove;
            }
        }
    
    
        return chipsToRemove;
    }

}
