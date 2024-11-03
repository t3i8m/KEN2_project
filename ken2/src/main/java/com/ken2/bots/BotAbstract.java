package com.ken2.bots;

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

}
