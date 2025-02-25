package com.ken2_1.Game_Components.Board;

public class Ring implements PlayObj{
    private String colour;

    public Ring(String colour){
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

    @Override
    public PlayObj clone() {
        return new Ring(this.colour); 
    }
}
