package com.ken2.Game_Components.Board;

public class Coin implements PlayObj{
    private String colour;

    public Coin(String colour){
        this.colour = colour;
    }

    @Override
    public String getColour(){
        return colour;
    }

    @Override
    public void setColour(String colour){
        this.colour = colour;
    }
}
