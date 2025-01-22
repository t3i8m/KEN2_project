package com.ken2.Game_Components.Board;


public class Coin implements PlayObj{
    private String colour;
    private int vertex;

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
    }

    @Override
    public PlayObj clone() {
        return new Coin(this.colour); 
    }

    public void setVertex(int vertex) {
        this.vertex = vertex;
    }

    public int getVertex() {
        return this.vertex;
    }
}
