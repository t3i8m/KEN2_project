package com.ken2.Game_Components.Board;

import javafx.scene.paint.Color;

public class Coin implements PlayObj{
    private String colour;

    public Coin(String color){
        this.colour = color;
    }

    @Override
    public String getColour(){
        return colour;
    }

    @Override
    public void setColour(String colour){
        this.colour = colour;
    }
    public void flipCoin() {
        if (this.colour.equalsIgnoreCase("black")) {
            this.colour = "white";
        } else {
            this.colour = "black";
        }
        System.out.println("Coin color flipped to: " + this.colour);
    }
}
