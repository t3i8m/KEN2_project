package com.ken2.bots;

import java.util.ArrayList;
import java.util.Random;

import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.GameState;

// abstract class for all of the bots
public abstract class BotAbstract implements Bot{
    private String color;

    public BotAbstract(String color){
        this.color = color;
    }
    
    @Override
    public String getColor(){
        return this.color;
    }

    // randomly selects a ring to remove
    @Override
    public Vertex removeRing(GameState state){
        Random random = new Random();
        ArrayList<Vertex> coordinatesOfTheRings = state.getAllVertexOfColor(this.getColor());
        if (coordinatesOfTheRings == null || coordinatesOfTheRings.isEmpty()) {
            System.out.println("No rings available for the color: " + this.getColor());
            return null;
        }
        System.out.println("-------------------------------");
        Vertex potentialRing = coordinatesOfTheRings.get(random.nextInt(coordinatesOfTheRings.size()));
        System.out.println("BOT WANTS TO REMOVE RING "+potentialRing.getVertextNumber());

        return potentialRing;
    }

}
